package ru.rbs.stats.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.rbs.stats.Stats;
import ru.rbs.stats.data.ReportParams;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAsync
@EnableScheduling
public class StatsReportSchedulingConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(StatsReportSchedulingConfiguration.class);

    // TODO: make these configs manageable via interface and store in DB
    private final List<ReportParams> configs = new ArrayList<ReportParams>();

    @Autowired
    private Stats stats;

    public StatsReportSchedulingConfiguration() {
        configs.add(new ReportParams("merchant_daily_counts", "1 DAYS"));
    }

    @Bean
    public Stats getStats() {
        return new Stats();
    }

    //@Scheduled(fixedRate = 1000)
    public void timeToBuildStatsReportsMaybe () {
        for (ReportParams config : configs) {
            if (config.getLastRun() == null) config.setLastRun(LocalDateTime.now().minusSeconds(config.getPeriodSeconds()));
            if (LocalDateTime.now().isAfter(config.getLastRun().plusSeconds(config.getPeriodSeconds()))) {
                // It's time! lets build a report.
                buildReport(config);
            }
        }
    }


    private void buildReport(ReportParams config) {
        stats.calculateAndReportStats(config, false);
    }

    public List<ReportParams> getConfigs() {
        return configs;
    }
}
