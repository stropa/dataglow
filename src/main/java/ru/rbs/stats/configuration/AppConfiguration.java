package ru.rbs.stats.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import ru.rbs.stats.configuration.dev.TestDatabaseConfiguration;

@Configuration
@Import({
        DatabaseConfiguration.class,
        TestDatabaseConfiguration.class,
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
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().addValueReader(new RecordValueReader());
        return mapper;
    }

}
