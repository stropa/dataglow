package ru.rbs.stats.configuration;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import ru.rbs.stats.data.InfluxDBCubeDataSource;
import ru.rbs.stats.data.TimedCubeDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    public static final String DATABASE_NAME = "play";

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
        return InfluxDBFactory.connect("http://localhost:8086", "root", "root");
    }

    @Bean(name = "timedCubeDataSource")
    public TimedCubeDataSource getTimedCubeDataSource() {
        return new InfluxDBCubeDataSource(influxDB, DATABASE_NAME);
    }

    @Bean(name = "dataSource")
    @Profile("!embedded-test-database")
    public DataSource getDataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        dsLookup.setResourceRef(true);
        return dsLookup.getDataSource("jdbc/GlowDataSource");
    }

    @Bean(name = "appDataSource")
    public DataSource getAppDataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        dsLookup.setResourceRef(true);
        return dsLookup.getDataSource("jdbc/GlowSelfDataSource");
    }

    @Bean
    public DSLContext getDslContext() {
        return new DefaultDSLContext(appDataSource, systemProperties.getJooqSQLDialect());
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

}
