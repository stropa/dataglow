package org.dataglow.v1.configuration.dev;

import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.dataglow.v1.db.hsqldb.HyperSqlDbServer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile("embedded-test-database") // !!! VERY IMPORTANT !!! NEVER REMOVE THIS ANNOTATION !!! Otherwise, we can accidentally corrupt user data
public class TestDatabaseConfiguration {


    @Bean
    public HyperSqlDbServer getTestDatabaseServer() {

        Properties props = new Properties();
        props.setProperty("server.database.0", "file:hsqldb/testdata");
        props.setProperty("server.dbname.0", "testdata");
        props.setProperty("server.remote_open", "true");
        props.setProperty("hsqldb.reconfig_logging", "false");

        HyperSqlDbServer hyperSqlDbServer = new HyperSqlDbServer(props);
        hyperSqlDbServer.start();

        return hyperSqlDbServer;
    }

    @Bean(name = "dataSource")
    public DataSource getTestDataJdbcTemplate() {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setJdbcUrl("jdbc:hsqldb:file:hsqldb/testdata");
        dataSource.setDriverClass("org.hsqldb.jdbc.JDBCDriver");
        return dataSource;
    }

    /*@Bean
    public TestDataLoader getTestDataLoader() {
        return new TestDataLoader();
    }*/

}
