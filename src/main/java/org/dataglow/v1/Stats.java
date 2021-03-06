package org.dataglow.v1;


import org.dataglow.v1.analyze.Artifact;
import org.dataglow.v1.analyze.alg.TripleSigmaRule;
import org.dataglow.v1.data.*;
import org.dataglow.v1.data.cache.CacheManager;
import org.dataglow.v1.data.cache.CachedSerieContainer;
import org.dataglow.v1.data.cache.TimeSeriesCache;
import org.dataglow.v1.service.ArtifactsService;
import org.dataglow.v1.store.CubeCoordinates;
import org.dataglow.v1.store.CubeDescription;
import org.dataglow.v1.store.CubeSchemaProvider;
import org.dataglow.v1.utils.CompositeName;
import org.influxdb.InfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.PatternMatchUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
    private CacheManager cacheManager = new CacheManager();



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
        while (nextPeriodEnd.isBefore(end.plusNanos(1000))) {
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

            cubeDataSource.sendToStorage(report, nextPeriodEnd);

            nextPeriodEnd = nextPeriodEnd.plusSeconds(periodSeconds);
            nextPeriodStart = nextPeriodStart.plusSeconds(periodSeconds);
            part++;

        }

    }

    private void runOnlineAnalyzers(ReportParams config, Report report, LocalDateTime periodStart, LocalDateTime periodEnd, String whatToAnalyze) {
        if (config.isAnalyzeAll()) {
            CachedSerieDataProvider provider = new CachedSerieDataProvider(cacheManager.getCache(config.getReportName()).getCachedSeries());
            for (ReportEntry entry : report.getEntries()) {
                for (String aggregateName : report.getCubeDescription().getAggregates()) {
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
        TimeSeriesCache cache = cacheManager.getCache(reportParams.getReportName());
        long updateCounter = cache.incrementUpdateCounter();
        cleanExpiredCacheEntries(reportParams.getCacheAge());
        for (ReportEntry entry : report.getEntries()) {
            for (String aggregateName : report.getCubeDescription().getAggregates()) {
                CompositeName serieName = buildSerieName(entry, report.getCubeDescription());
                serieName = serieName.withPart(aggregateName);
                if (reportParams.isCacheAll() ||
                        (reportParams.isUseCache()
                                && PatternMatchUtils.simpleMatch(reportParams.getCacheMask(), serieName.format()))) {
                    Map<CompositeName, CachedSerieContainer> cachedSeries = cache.getCachedSeries();
                    CachedSerieContainer container = cachedSeries.get(serieName);
                    if (container == null) {
                        container = new CachedSerieContainer(serieName, reportParams.getMaxCacheSize());
                        cachedSeries.put(serieName, container);
                    }
                    container.putValue(time, entry.getAggregates().get(report.getCubeDescription().getAggregates().indexOf(aggregateName)));
                }
            }
        }
        logger.info("Finished updating cached series");
    }

    private void cleanExpiredCacheEntries(Duration cacheAge) {
        if (cacheAge == null) {
            return;
        }
        for (String cacheName : cacheManager.getCacheNames()) {
            Map<CompositeName, CachedSerieContainer> cachedSeries = cacheManager.getCache(cacheName).getCachedSeries();
            for (CompositeName serieName : cachedSeries.keySet()) {
                List<LocalDateTime> toRemove = new ArrayList<>();
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

    public Map<CompositeName, CachedSerieContainer> getCachedSeries(String cubeName) {
        return cacheManager.getCache(cubeName).getCachedSeries();
    }
}
