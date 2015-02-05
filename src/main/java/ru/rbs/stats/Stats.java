package ru.rbs.stats;


import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.PatternMatchUtils;
import ru.rbs.stats.analyze.Artifact;
import ru.rbs.stats.analyze.TimeSeriesAnalyzeConfig;
import ru.rbs.stats.analyze.alg.TripleSigmaRule;
import ru.rbs.stats.configuration.DatabaseConfiguration;
import ru.rbs.stats.data.*;
import ru.rbs.stats.data.cache.CachedSerieContainer;
import ru.rbs.stats.service.ArtifactsService;
import ru.rbs.stats.store.CubeCoordinates;
import ru.rbs.stats.store.CubeDescription;
import ru.rbs.stats.store.CubeSchemaProvider;
import ru.rbs.stats.utils.CompositeName;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class Stats implements CubeSchemaProvider {

    private static final Logger logger = LoggerFactory.getLogger(Stats.class);

    @Autowired
    private InfluxDB influxDB;

    @Resource
    private ArtifactsService artifactsService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Resource
    private TimedCubeDataSource cubeDataSource;

    private Map<String, StatsReportBuilder> reportBuilders = new HashMap<String, StatsReportBuilder>();

    private static MetricsRegistry metricsRegistry = new MetricsRegistry();
    private Map<CompositeName, CachedSerieContainer> cachedSeries = new HashMap<CompositeName, CachedSerieContainer>();


    @PostConstruct
    public void init() {
        //reportBuilders.put("merchantDailyCounts", new SQLReportBuilder(jdbcTemplate));



        final CubeCoordinates c = new CubeCoordinates("merchant.operation.actionCode");
        c.setAxis("merchant", "bsigroup");
        c.setAxis("operation", "AUTHORIZATION_FINISHED");
        c.setAxis("actionCode", "0");
        c.setVarName("amount");

        final TimeSeriesAnalyzeConfig firstAnalyzer = new TimeSeriesAnalyzeConfig("try", c, new TripleSigmaRule(30), 86400L);
        firstAnalyzer.setJob(new Runnable() {
            @Override
            public void run() {
                // fetch series from database and apply algorithm
                LocalDateTime periodStart = firstAnalyzer.getLastRun();
                LocalDateTime periodEnd = firstAnalyzer.getLastRun().plusSeconds(firstAnalyzer.getPeriodSeconds());

                /*List<Artifact> artifacts = firstAnalyzer.getAlgorithm().apply(cubeDataSource, c, periodStart, periodEnd);

                for (Artifact artifact : artifacts) {
                    logger.debug("ARTIFACT DISCOVERED!!!: " + artifact + " in cube: " + c);
                }*/
            }
        });
        metricsRegistry.addSeriesAnalyzer(firstAnalyzer);
    }


    public void process(ReportParams config, boolean unscheduled, LocalDateTime from, LocalDateTime to,
                        boolean cache, String whatToAnalyze) {
        StatsReportBuilder reportBuilder = reportBuilders.get(config.getReportName());
        if (reportBuilder == null) {
            logger.error("No StatsReportBuilder for name {}", config.getReportName());
            return;
        }
        long periodSeconds = config.getPeriodSeconds();
        LocalDateTime nextPeriodStart = from == null ? config.getLastRun() : from;
        LocalDateTime nextPeriodEnd = nextPeriodStart.plusSeconds(periodSeconds);
        LocalDateTime end = to == null ? nextPeriodEnd : to;

        int part = 1;
        while (nextPeriodEnd.isBefore(end.minusSeconds(periodSeconds))) {
            // build report for next period part
            logger.debug("It's " + LocalDateTime.now() + " now and we are going to fetch stats report from " + nextPeriodStart + " to " + nextPeriodEnd);
            Report report = reportBuilder.buildReport(part > 1 ? nextPeriodStart.plusNanos(1000) : nextPeriodStart, nextPeriodEnd);
            if (!unscheduled) {
                config.setLastRun(nextPeriodEnd);
            }
            if (cache) {
                updateCache(report, nextPeriodEnd, config);
            }
            if (isNotBlank(whatToAnalyze)) {
                runOnlineAnalyzers(config, report, nextPeriodStart, nextPeriodEnd, whatToAnalyze);
            }

            sendToStorage(report, nextPeriodEnd);

            nextPeriodEnd = nextPeriodEnd.plusSeconds(periodSeconds);
            nextPeriodStart = nextPeriodStart.plusSeconds(periodSeconds);
            part++;

        }

    }

    private void runOnlineAnalyzers(ReportParams config, Report report, LocalDateTime periodStart, LocalDateTime periodEnd, String whatToAnalyze) {
        if (config.isAnalyzeAll()) {
            CachedSerieDataProvider provider = new CachedSerieDataProvider(cachedSeries);
            for (ReportEntry entry : report.getEntries()) {
                for (String aggregateName : report.getCubeDescription().getAgregates()) {
                    CubeCoordinates coordinates = coordinatesForEntry(entry, report.getCubeDescription());
                    coordinates.setVarName(aggregateName);

                    CompositeName serieName = buildSerieName(entry, report.getCubeDescription());
                    serieName = serieName.withPart(aggregateName);
                    provider.setSerieName(serieName);
                    // TODO: make Analyzers (algorithms) selectable for report
                    List<Artifact> artifacts = new TripleSigmaRule(30).apply(provider, periodStart, periodEnd);
                    artifacts.forEach(a -> {
                        a.setCoordinates(coordinates);
                        artifactsService.persist(a);
                    });
                }
            }
        }
    }

    private CubeCoordinates coordinatesForEntry(ReportEntry entry, CubeDescription cubeDescription) {
        CubeCoordinates coordinates = new CubeCoordinates(cubeDescription.getName());
        for (int i = 0; i < cubeDescription.getDimensions().size(); i++) {
            coordinates.setAxis(cubeDescription.getDimensions().get(i), entry.getProfile().get(i));
        }
        return coordinates;
    }

    private void updateCache(Report report, LocalDateTime time, ReportParams reportParams) {
        // TODO: move to some production-ready cache implementation. For now we start with the simplest one. Dummy and slow.
        cleanExpiredCacheEntries(reportParams.getCacheAge());
        for (ReportEntry entry : report.getEntries()) {
            for (String aggregateName : report.getCubeDescription().getAgregates()) {
                CompositeName serieName = buildSerieName(entry, report.getCubeDescription());
                serieName = serieName.withPart(aggregateName);
                if (reportParams.isCacheAll() ||
                        (reportParams.isUseCache()
                                && PatternMatchUtils.simpleMatch(reportParams.getCacheMask(), serieName.format()))) {
                    CachedSerieContainer container = cachedSeries.get(serieName);
                    if (container == null) {
                        container = new CachedSerieContainer(serieName.format(), reportParams.getMaxCacheSize());
                        cachedSeries.put(serieName, container);
                    }
                    container.putValue(time, entry.getAggregates().get(report.getCubeDescription().getAgregates().indexOf(aggregateName)));
                }
            }
        }
        logger.info("Finished updating cached series");
    }

    private void cleanExpiredCacheEntries(Duration cacheAge) {
        if (cacheAge == null) {
            return;
        }
        for (CompositeName serieName : cachedSeries.keySet()) {
            List<LocalDateTime> toRemove = new ArrayList<LocalDateTime>();
            SortedMap<LocalDateTime, Number> serie = cachedSeries.get(serieName).getSerie();
            Set<LocalDateTime> times = serie.keySet();
            for (LocalDateTime t : times) {
                if (t.isBefore(LocalDateTime.now().minus(cacheAge))) {
                    toRemove.add(t);
                }
            }
            for (LocalDateTime removing : toRemove) {
                serie.remove(removing);
            }
        }
    }

    private CompositeName buildSerieName(ReportEntry entry, CubeDescription cubeDescription) {
        CompositeName name = CompositeName.fromParts(cubeDescription.getName());
        for (Object dimensionValue : entry.getProfile()) {
            name = name.withPart(dimensionValue.toString());
        }
        return name;
    }

    public static CompositeName buildSerieName(CubeCoordinates coordinates) {
        CompositeName name = CompositeName.fromParts(coordinates.getCubeName());
        for (Object dimensionValue : coordinates.getProfile().values()) {
            name = name.withPart(dimensionValue.toString());
        }
        name = name.withPart(coordinates.getVarName());
        return name;
    }


    public void sendToStorage(Report report, LocalDateTime time) {
        Serie.Builder builder = new Serie.Builder(report.getCubeDescription().getName());

        List<String> allColumns = new ArrayList<String>();
        allColumns.addAll(report.getCubeDescription().getDimensions());
        allColumns.addAll(report.getCubeDescription().getAgregates());
        allColumns.add("time");
        builder.columns(allColumns.toArray(new String[allColumns.size()]));

        Instant instant = ((ChronoZonedDateTime) time.atZone(ZoneId.systemDefault())).toInstant();
        Date timeSoSend = Date.from(instant);
        for (ReportEntry entry : report.getEntries()) {
            List vals = new ArrayList();
            vals.addAll(entry.getProfile());
            vals.addAll(entry.getAggregates());
            vals.add(timeSoSend.getTime() / 1000);
            builder.values(vals.toArray());
        }
        Serie serie = builder.build();

        influxDB.write(DatabaseConfiguration.DATABASE_NAME, TimeUnit.SECONDS, serie);
        logger.info("A serie " + serie.getName() + " of " + serie.getRows() + " rows was saved to storage");
    }

    public Map<String, StatsReportBuilder> getReportBuilders() {
        return reportBuilders;
    }

    public MetricsRegistry getMetricsRegistry() {
        return metricsRegistry;
    }

    @Override
    public CubeDescription getCubeSchema(String cubeName) {
        //return metricsRegistry.getCubes().get(cubeName);
        StatsReportBuilder reportBuilder = reportBuilders.get(cubeName);
        if (reportBuilder == null) return null;
        return reportBuilder.getConfig().getCubeDescription();
    }

    public ReportParams getReportParams(String cubeName) {
        StatsReportBuilder reportBuilder = reportBuilders.get(cubeName);
        if (reportBuilder == null) return null;
        return reportBuilder.getConfig();
    }

    public Map<CompositeName, CachedSerieContainer> getCachedSeries() {
        return cachedSeries;
    }
}
