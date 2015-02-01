package ru.rbs.stats.configuration;

import org.jooq.SQLDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rbs.stats.configuration.options.DatabaseUsed;

import java.util.Properties;

@Service
public class SystemProperties {

    @Autowired
    private Properties settings;

    public static final String SCHEMA = "schema";
    public static final String DATA_SOURCE_DATABASE_TYPE = "data.source.database.type";

    public SQLDialect getJooqSQLDialect() {
        switch (getApplicationDatabaseType()) {
            case PostgreSQL:
                return SQLDialect.POSTGRES;
            case Oracle:
                if (settings.getProperty("database.version").contains("10")) {
                    return SQLDialect.ORACLE;
                } else if (settings.getProperty("database.version").contains("11")) {
                    return SQLDialect.ORACLE11G;
                } else if (settings.getProperty("database.version").contains("12")) {
                    return SQLDialect.ORACLE12C;
                } return SQLDialect.ORACLE;
            case HSQLDB:
                return SQLDialect.HSQLDB;
            default:
                return SQLDialect.SQL99;
        }
    }


    public DatabaseUsed getDataSourceDatabaseType() {
        return DatabaseUsed.valueOf(settings.getProperty(DATA_SOURCE_DATABASE_TYPE));
    }

    public DatabaseUsed getApplicationDatabaseType() {
        return DatabaseUsed.valueOf(settings.getProperty("app.database.type"));
    }

    public String getProperty(String name) {
        return settings.getProperty(name);
    }

}
