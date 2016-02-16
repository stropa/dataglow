package org.dataglow.v1.configuration;

import com.jolbox.bonecp.BoneCPDataSource;
import org.dataglow.v1.data.InfluxDBCubeDataSource;
import org.dataglow.v1.data.TimedCubeDataSource;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Resource(name = "appDataSource")
    private DataSource appDataSource;

    @Autowired
    private SystemProperties systemProperties;

    @Resource
    private InfluxDB influxDB;

    @Bean
    public InfluxDB getInfluxDB() {
        return InfluxDBFactory.connect("http://10.77.5.161:8086", "root", "root");
    }

    @Bean(name = "timedCubeDataSource")
    public TimedCubeDataSource getTimedCubeDataSource() {
        return new InfluxDBCubeDataSource(influxDB, systemProperties.getProperty(SystemProperties.INFLUXDB_DATABASE_NAME));
    }

    @Bean(name = "dataSource") // business data
    @Profile(value = {"use-jndi-datasource"})
    public DataSource getJndiDataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        dsLookup.setResourceRef(true);
        return dsLookup.getDataSource("jdbc/GlowDataSource");
    }

    @Bean(name = "appDataSource") // application own data
    public DataSource getAppDataSource() {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setJdbcUrl("jdbc:hsqldb:file:hsqldb/dataglowapp");
        dataSource.setDriverClass("org.hsqldb.jdbc.JDBCDriver");
        return dataSource;
    }

    @Bean
    public DSLContext getDslContext() {
        return new DefaultDSLContext(appDataSource, systemProperties.getJooqSQLDialect());
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void initAppDBSchemaIfFirstStart() {

        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setScripts(new ClassPathResource("schema.sql"));
        populator.execute(appDataSource);
    }

}
