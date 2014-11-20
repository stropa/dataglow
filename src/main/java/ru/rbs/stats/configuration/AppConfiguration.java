package ru.rbs.stats.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        DatabaseConfiguration.class,
        MvcConfiguration.class,
        StatsReportSchedulingConfiguration.class
        })

public class AppConfiguration {
}
