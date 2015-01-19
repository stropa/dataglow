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
    public static final String DATABASE_USED = "database.used";

    public SQLDialect getJooqSQLDialect() {
        switch (getDatabaseUsed()) {
            case PostgreSQL:
                return SQLDialect.POSTGRES;
            case Oracle:
                if (settings.getProperty("database.version").contains("10")) {
                    return SQLDialect.ORACLE;
                } else if (settings.getProperty("database.version").contains("11")) {
                    return SQLDialect.ORACLE11G;
                } else if (settings.getProperty("database.version").contains("12")) {
                    return SQLDialect.ORACLE12C;
                }
            default:
                return SQLDialect.SQL99;
        }
    }


    public DatabaseUsed getDatabaseUsed() {
        return DatabaseUsed.valueOf(settings.getProperty(DATABASE_USED));
    }

    public String getProperty(String name) {
        return settings.getProperty(name);
    }

}
