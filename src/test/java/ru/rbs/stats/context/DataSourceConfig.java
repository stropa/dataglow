package ru.rbs.stats.context;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rbs.stats.data.SQLCubeDescriptionParser;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
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
