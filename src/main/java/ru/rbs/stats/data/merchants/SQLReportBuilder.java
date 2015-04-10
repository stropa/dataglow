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

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

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
                ReportEntry entry = new ReportEntry();
                for (String dimensionName : config.getCubeDescription().getDimensions()) {
                    entry.getProfile().add(rs.getString(dimensionName));
                }
                for (String aggregateName : config.getCubeDescription().getAggregates()) {
                    switch (config.getCubeDescription().getType(aggregateName)) {
                        case INTEGER:
                            entry.getAggregates().add(rs.getLong(aggregateName));
                            break;
                        case FLOAT:
                            entry.getAggregates().add(rs.getFloat(aggregateName));
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

    @Override
    public ReportParams getConfig() {
        return config;
    }

}
