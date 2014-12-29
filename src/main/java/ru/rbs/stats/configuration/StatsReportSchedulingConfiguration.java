package ru.rbs.stats.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.rbs.stats.Stats;
import ru.rbs.stats.data.ReportParams;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableAsync
@EnableScheduling
public class StatsReportSchedulingConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(StatsReportSchedulingConfiguration.class);

    // TODO: make these reports manageable via interface and store in DB
    private final List<ReportParams> reports = new ArrayList<ReportParams>();

    @Autowired
    private Stats stats;

    public StatsReportSchedulingConfiguration() {

    }

    @Bean
    public Stats getStats() {
        return new Stats();
    }

    @PostConstruct
    public void init() {
        final ReportParams merchantDailyCounts = new ReportParams("merchantDailyCounts", "1 MINUTES");
        merchantDailyCounts.setCubeDescription(stats.getMetricsRegistry().getCubes().get("merchant.operation.actionCode"));
        merchantDailyCounts.setJob(new Runnable() {
            @Override
            public void run() {
                stats.calculateAndReportStats(merchantDailyCounts, false);
            }
        });
        //reports.add(merchantDailyCounts);
    }

    @Scheduled(fixedRate = 1000)
    public void timeToWork() {
        runJobs(reports);
        //runJobs(stats.getMetricsRegistry().getAnalyzers());
    }

    private void runJobs(Collection<? extends Schedulable> jobConfigs) {
        for (Schedulable config : jobConfigs) {
            LocalDateTime now = LocalDateTime.now();

            //for debug
            //now = now.minusDays(39);

            if (config.getLastRun() == null) config.setLastRun(now.minusSeconds(config.getPeriodSeconds()));
            if (now.isAfter(config.getLastRun().plusSeconds(config.getPeriodSeconds()))) {
                // It's time! lets build a report.
                try {
                    config.runJob();
                } finally {
                    config.setLastRun(now);
                }
            }
        }
    }


    public List<ReportParams> getReports() {
        return reports;
    }
}
