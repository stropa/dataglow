package org.dataglow.v1.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.dataglow.v1.configuration.dev.TestDatabaseConfiguration;

@Configuration
@Import({
        DatabaseConfiguration.class,
        TestDatabaseConfiguration.class,
        JooqDaosConfiguration.class,
        MvcConfiguration.class,
        WebSecurityConfiguration.class,
        StatsReportSchedulingConfiguration.class
        })
@ImportResource("file:${components}")
public class AppConfiguration {

    @Bean(name = "settings")
    public PropertiesFactoryBean getProperties() {
        PropertiesFactoryBean props = new PropertiesFactoryBean();
        String configFile = System.getProperty("config");
        props.setLocations(configFile != null ? new FileSystemResource(configFile)
                : new ClassPathResource("config.properties"));
        return props;
    }

    @Bean(name = "mapper")
    public ModelMapper getModelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().addValueReader(new RecordValueReader());
        return mapper;
    }

}
