package ru.rbs.stats.configuration;

import org.jooq.SQLDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.rbs.stats.configuration.options.DatabaseUsed;

import java.lang.reflect.Field;
import java.util.Properties;

@Service
public class SystemProperties {

    @Autowired
    private Properties settings;

    public static final String SCHEMA = "schema";
    public static final String DATA_SOURCE_DATABASE_TYPE = "data.source.database.type";
    public static final String INFLUXDB_DATABASE_NAME = "influxdb.database.name";

    public SQLDialect getJooqSQLDialect() {
        switch (getApplicationDatabaseType()) {
            case PostgreSQL:
                return loadDialect("POSTGRES");
            case Oracle:
                if (settings.getProperty("database.version").contains("10")) {
                    return loadDialect("ORACLE");
                } else if (settings.getProperty("database.version").contains("11")) {
                    return loadDialect("ORACLE11G");
                } else if (settings.getProperty("database.version").contains("12")) {
                    return loadDialect("ORACLE12C");
                } return loadDialect("ORACLE");
            case HSQLDB:
                return loadDialect("HSQLDB");
            default:
                return loadDialect("SQL99");
        }
    }

    private SQLDialect loadDialect(String dialectName) {
        Field field = ReflectionUtils.findField(SQLDialect.class, dialectName);
        String exceptionMessage = "Can't init database dialect [" + dialectName +
                "] in JOOQ. Maybe it's for commercial use only. See http://www.jooq.org/download/ ";
        if (field == null) {
            throw new IllegalStateException(exceptionMessage);
        }
        try {
            return (SQLDialect) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException(exceptionMessage, e);
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
