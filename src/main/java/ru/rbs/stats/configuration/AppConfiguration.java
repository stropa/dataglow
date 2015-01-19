package ru.rbs.stats.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Import({
        DatabaseConfiguration.class,
        JooqDaosConfiguration.class,
        MvcConfiguration.class,
        StatsReportSchedulingConfiguration.class
        })

public class AppConfiguration {

    @Bean(name = "settings")
    public PropertiesFactoryBean getProperties() {
        PropertiesFactoryBean props = new PropertiesFactoryBean();
        props.setLocations(new ClassPathResource("config.properties"));
        return props;
    }

    @Bean(name = "mapper")
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

}
