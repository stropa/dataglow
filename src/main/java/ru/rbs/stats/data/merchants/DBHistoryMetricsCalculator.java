package ru.rbs.stats.data.merchants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import ru.rbs.stats.data.StatsReportBuilder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class DBHistoryMetricsCalculator implements StatsReportBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DBHistoryMetricsCalculator.class);

    private JdbcTemplate jdbcTemplate;

    private static final String QUERY = "select count(*), m.merchantlogin, sum(h.amount), h.state, h.action_code " +
            "  from bpc_transaction_history h " +
            "  join bpctransactions bt on (h.mdorder = bt.mdorder) " +
            "  join bpcmerchantdetails m on (bt.merchant_id = m.id) " +
            "  where h.date_time between ? and ? " +
            "    and h.state in ('AUTHORIZATION_FINISHED', 'DEPOSIT_FINISHED', 'REVERSAL_FINISHED', 'REFUND_FINISHED')" +
            "  group by m.merchantlogin, h.state, h.action_code " +
            "order by sum(h.amount) desc";

    public DBHistoryMetricsCalculator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReportEntry> report(final LocalDateTime periodStart, final LocalDateTime periodEnd) {

        List<ReportEntry> reportEntryList = jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(QUERY);
                statement.setTimestamp(1, Timestamp.valueOf(periodStart));
                statement.setTimestamp(2, Timestamp.valueOf(periodEnd));
                //logger.debug("Statement: " + statement);


                return statement;
            }
        }, new RowMapper<ReportEntry>() {
            @Override
            public ReportEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ReportEntry(rs.getLong(1), rs.getLong(3), rs.getString(2), rs.getString(4), rs.getInt(5));
            }
        });
        logger.debug("Fetched {} rows", reportEntryList.size());
        return reportEntryList;
    }

}
