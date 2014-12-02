package ru.rbs.stats;


import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.rbs.stats.analyze.Artefact;
import ru.rbs.stats.analyze.TimeSeriesAnalyzeConfig;
import ru.rbs.stats.analyze.alg.TripleSigmaRule;
import ru.rbs.stats.data.InfluxDBCubeDataSource;
import ru.rbs.stats.data.ReportParams;
import ru.rbs.stats.data.StatsReportBuilder;
import ru.rbs.stats.data.TimedCubeDataSource;
import ru.rbs.stats.data.merchants.DBHistoryMetricsCalculator;
import ru.rbs.stats.data.merchants.ReportEntry;
import ru.rbs.stats.store.CubeCoordinates;
import ru.rbs.stats.store.CubeDescription;
import ru.rbs.stats.store.CubeSchemaProvider;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class Stats implements CubeSchemaProvider {

    private static final Logger logger = LoggerFactory.getLogger(Stats.class);

    @Autowired
    private InfluxDB influxDB;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private TimedCubeDataSource cubeDataSource;

    private Map<String, StatsReportBuilder> reportBuilders = new HashMap<String, StatsReportBuilder>();

    private static MetricsRegistry metricsRegistry = new MetricsRegistry();


    @PostConstruct
    public void init() {
        reportBuilders.put("merchant_daily_counts", new DBHistoryMetricsCalculator(jdbcTemplate));

        cubeDataSource = new InfluxDBCubeDataSource(influxDB, "play", this);

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

                List<Artefact> artefacts = firstAnalyzer.getAlgorithm().apply(cubeDataSource, c, periodStart, periodEnd);

                for (Artefact artefact : artefacts) {
                    logger.debug("ARTEFACT DISCOVERED!!!: " + artefact + " in cube: " + c);
                }
            }
        });


        metricsRegistry.addSeriesAnalyzer(firstAnalyzer);
        CubeDescription firstTestCube = new CubeDescription("merchant.operation.actionCode");
        firstTestCube.setDimensions(asList("merchant", "operation", "actionCode"));
        firstTestCube.setAgregates(asList("count", "amount"));
        HashMap<String, CubeDescription.CubeDataType> types = new HashMap<String, CubeDescription.CubeDataType>();
        types.put("merchant", CubeDescription.CubeDataType.STRING);
        types.put("operation", CubeDescription.CubeDataType.STRING);
        types.put("actionCode", CubeDescription.CubeDataType.INTEGER);
        firstTestCube.setTypes(types);
        metricsRegistry.addCubeDescription(firstTestCube);
    }




    public void calculateAndReportStats(ReportParams config, boolean unscheduled) {
        StatsReportBuilder reportBuilder = reportBuilders.get(config.getReportName());
        if (reportBuilder == null) {
            logger.error("No StatsReportBuilder for name {}", config.getReportName());
            return;
        }
        LocalDateTime periodStart = config.getLastRun();
        LocalDateTime periodEnd = config.getLastRun().plusSeconds(config.getPeriodSeconds());
        logger.debug("It's " + LocalDateTime.now() + " now and we are going to fetch stats report from " + periodStart + " to " + periodEnd);
        if (!unscheduled) {
            config.setLastRun(periodEnd);
        }

        List<ReportEntry> report = reportBuilder.report(periodStart, periodEnd);

        sendToStorage(report, periodEnd);

    }


    public void sendToStorage(List<ReportEntry> report, LocalDateTime time) {
        Serie.Builder builder = new Serie.Builder(ReportEntry.getName());
        List<String> columns = asList(ReportEntry.getColumns());
        List<String> allColumns = new ArrayList<String>(columns);
        allColumns.add("time");
        builder.columns(allColumns.toArray(new String[allColumns.size()]));
        Instant instant = ((ChronoZonedDateTime) time.atZone(ZoneId.systemDefault())).toInstant();
        Date timeSoSend = Date.from(instant);
        for (ReportEntry entry : report) {
            builder.values(entry.getMerchant(), entry.getOperation(), entry.getActionCode(), entry.getCount(),
                    entry.getAmount(), timeSoSend.getTime() / 1000);
        }
        Serie serie = builder.build();

        influxDB.write("play", TimeUnit.SECONDS, serie);
    }

    public Map<String, StatsReportBuilder> getReportBuilders() {
        return reportBuilders;
    }

    public MetricsRegistry getMetricsRegistry() {
        return metricsRegistry;
    }

    @Override
    public CubeDescription getCubeSchema(String cubeName) {
        return metricsRegistry.getCubes().get(cubeName);
    }
}
