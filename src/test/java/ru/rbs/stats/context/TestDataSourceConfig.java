package ru.rbs.stats.context;

import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

}
