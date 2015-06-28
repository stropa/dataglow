package ru.rbs.stats.context;

import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.rbs.stats.configuration.SystemProperties;
import ru.rbs.stats.data.SQLCubeDescriptionParser;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@Configuration
public class TestDataSourceConfig {

    @Bean
    public DataSource getDataSource() throws PropertyVetoException {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://127.0.0.1:5432/rbsbase");
        dataSource.setDriverClass("org.postgresql.Driver");
        dataSource.setPassword("rbs");
        dataSource.setUser("rbs");
        return dataSource;
    }

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
