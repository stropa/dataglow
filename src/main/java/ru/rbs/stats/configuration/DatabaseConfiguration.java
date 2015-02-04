package ru.rbs.stats.configuration;

import com.jolbox.bonecp.BoneCPDataSource;
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

    @Bean
    public InfluxDB getInfluxDB() {
        return InfluxDBFactory.connect("http://localhost:8086", "root", "root");
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
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setJdbcUrl("jdbc:hsqldb:file:hsqldb/appdata");
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

}
