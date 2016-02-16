package org.dataglow.v1.context;

import org.dataglow.v1.configuration.SystemProperties;
import org.dataglow.v1.data.SQLCubeDescriptionParser;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class TestDataSourceConfig {


    @Bean
    public SQLCubeDescriptionParser getSqlCubeDescriptionParser() {
        return new SQLCubeDescriptionParser();
    }

    @Bean(name = "systemProperties")
    public SystemProperties getSystemProperties() {
        return new SystemProperties();
    }

    @Bean(name = "settings")
    public PropertiesFactoryBean getProperties() {
        PropertiesFactoryBean props = new PropertiesFactoryBean();
        props.setLocations(new Resource[] {new ClassPathResource("test.config.properties")});
        return props;
    }

}
