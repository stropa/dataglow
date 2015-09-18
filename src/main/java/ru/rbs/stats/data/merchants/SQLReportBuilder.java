package ru.rbs.stats.data.merchants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import ru.rbs.stats.data.Report;
import ru.rbs.stats.data.ReportEntry;
import ru.rbs.stats.data.ReportParams;
import ru.rbs.stats.data.StatsReportBuilder;
import ru.rbs.stats.store.CubeDescription;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.Types.*;

public class SQLReportBuilder implements StatsReportBuilder {

    private ReportParams config;

    private static final Logger logger = LoggerFactory.getLogger(SQLReportBuilder.class);

    private JdbcTemplate jdbcTemplate;

    public SQLReportBuilder(JdbcTemplate jdbcTemplate, final ReportParams config) {
        this.jdbcTemplate = jdbcTemplate;
        this.config = config;
    }


    @Override
    public Report buildReport(final LocalDateTime periodStart, final LocalDateTime periodEnd) {

        final boolean needToDiscoverColumnTypes = config.getCubeDescription().getTypes().isEmpty();
        List<ReportEntry> entries = jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(config.getQuery());
                statement.setTimestamp(1, Timestamp.valueOf(periodStart));
                statement.setTimestamp(2, Timestamp.valueOf(periodEnd));
                return statement;
            }
        }, new RowMapper<ReportEntry>() {
            @Override
            public ReportEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (needToDiscoverColumnTypes) {
                    discoverColumnTypes(config.getCubeDescription(), rs);
                }
                ReportEntry entry = new ReportEntry();
                for (String dimensionName : config.getCubeDescription().getDimensions()) {
                    entry.getProfile().add(rs.getString(dimensionName.toUpperCase())); // TODO: handle DB-specific casing
                }
                for (String aggregateName : config.getCubeDescription().getAggregates()) {
                    switch (config.getCubeDescription().getType(aggregateName)) {
                        case INTEGER:
                            entry.getAggregates().add(rs.getLong(aggregateName.toUpperCase()));
                            break;
                        case FLOAT:
                            entry.getAggregates().add(rs.getFloat(aggregateName.toUpperCase()));
                        case STRING:
                            logger.warn("Converting aggregate column {} from String", aggregateName);
                            entry.getAggregates().add(Float.valueOf(rs.getString(aggregateName.toUpperCase())));
                    }
                }
                return entry;
            }
        });
        logger.debug("Fetched {} rows", entries.size());
        Report report = new Report();
        report.setEntries(entries);
        report.setCubeDescription(config.getCubeDescription());
        return report;
    }

    private void discoverColumnTypes(CubeDescription cubeDescription, ResultSet rs) {
        try {
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columns.put(rs.getMetaData().getColumnName(i), i);
            }
            for (String aggregateName : cubeDescription.getAggregates()) {
                for (String columnName : columns.keySet()) {
                    if (columnName.equalsIgnoreCase(aggregateName)) {
                        Integer column = columns.get(columnName);
                        cubeDescription.getTypes().put(aggregateName,
                                selectCubeDataType(rs.getMetaData(), column));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private CubeDescription.CubeDataType selectCubeDataType(ResultSetMetaData metaData, int column) throws SQLException {
        int columnType = metaData.getColumnType(column);
        switch (columnType) {
            case BIT:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
                return CubeDescription.CubeDataType.INTEGER;
            case NUMERIC:
            case DECIMAL:
                return metaData.getScale(column) > 0 ? CubeDescription.CubeDataType.FLOAT : CubeDescription.CubeDataType.INTEGER;
            case FLOAT:
            case DOUBLE:
            case REAL:
                return CubeDescription.CubeDataType.FLOAT;
            default:
                return CubeDescription.CubeDataType.STRING;
        }
    }

    @Override
    public ReportParams getConfig() {
        return config;
    }

}
