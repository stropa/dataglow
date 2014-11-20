package ru.rbs.stats;


import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.rbs.stats.data.ReportParams;
import ru.rbs.stats.data.StatsReportBuilder;
import ru.rbs.stats.data.merchants.DBHistoryMetricsCalculator;
import ru.rbs.stats.data.merchants.ReportEntry;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Stats {

    private static final Logger logger = LoggerFactory.getLogger(Stats.class);

    @Autowired
    private InfluxDB influxDB;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Map<String, StatsReportBuilder> reportBuilders = new HashMap<String, StatsReportBuilder>();

    @PostConstruct
    public void init() {
        reportBuilders.put("merchant_daily_counts", new DBHistoryMetricsCalculator(jdbcTemplate));
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
        List<String> columns = Arrays.asList(ReportEntry.getColumns());
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
}
