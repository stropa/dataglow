package ru.rbs.stats.data;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.rbs.stats.context.DataSourceConfig;
import ru.rbs.stats.store.CubeDescription;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.rbs.stats.store.CubeDescription.CubeDataType;

@ContextConfiguration(classes = DataSourceConfig.class)

public class SQLCubeDescriptionParserTest extends AbstractTestNGSpringContextTests {

    @Resource
    private SQLCubeDescriptionParser sqlCubeDescriptionParser;

    @Resource
    private DataSource dataSource;

    // TODO: use neutral table and column names - create test schema
    private static final String QUERY = "select count(*), m.merchantlogin, sum(h.amount), h.state, h.action_code " +
            "  from bpc_transaction_history h " +
            "  join bpctransactions bt on (h.mdorder = bt.mdorder) " +
            "  join bpcmerchantdetails m on (bt.merchant_id = m.id) " +
            "  where h.date_time between ? and ? " +
            "    and h.state in ('AUTHORIZATION_FINISHED', 'DEPOSIT_FINISHED', 'REVERSAL_FINISHED', 'REFUND_FINISHED')" +
            "  group by m.merchantlogin, h.state, h.action_code " +
            "order by sum(h.amount) desc";

    private static final String QUERY_WITH_ALIASES = "select count(*) as total_count, m.merchantlogin as merchant_login," +
            " sum(h.amount) as sum_amount, h.state as hist_state, h.action_code as action_code  from bpc_transaction_history h" +
            "   join bpctransactions bt on (h.mdorder = bt.mdorder)   join bpcmerchantdetails m on (bt.merchant_id = m.id)" +
            "   where h.date_time between ? and ?     and h.state in ('AUTHORIZATION_FINISHED', 'DEPOSIT_FINISHED'," +
            " 'REVERSAL_FINISHED', 'REFUND_FINISHED')  group by m.merchantlogin, h.state, h.action_code " +
            "order by sum(h.amount) desc";

    @Test
    public void testGetTableNameForColumn() {
        String tableNameForColumn = new SQLCubeDescriptionParser().getTableNameForColumn("m.merchantlogin", QUERY);
        assertEquals(tableNameForColumn, "bpcmerchantdetails");
    }

    @Test
    public void testSelectType() throws SQLException {
        CubeDataType cubeDataType = sqlCubeDescriptionParser.selectType("merchantlogin", QUERY);
        assertEquals(cubeDataType, CubeDataType.STRING);

        cubeDataType = sqlCubeDescriptionParser.selectType("count(*)", QUERY);
        assertEquals(cubeDataType, CubeDataType.INTEGER);

        // TODO: add check for float type
    }

    @Test
    public void testParseCubeFromQuery() {
        CubeDescription cubeDescription = sqlCubeDescriptionParser.parseCubeFromQuery(QUERY, "cubeName");
        assertTrue(cubeDescription.getAgregates().containsAll(asList("sum_h_amount", "count_all")));
        assertTrue(cubeDescription.getDimensions().containsAll(asList("m_merchantlogin", "h_state", "h_action_code")));
        assertEquals(cubeDescription.getType("sum_h_amount"), CubeDataType.INTEGER);
        assertEquals(cubeDescription.getType("count_all"), CubeDataType.INTEGER);
        assertEquals(cubeDescription.getType("m_merchantlogin"), CubeDataType.STRING);
        assertEquals(cubeDescription.getType("h_action_code"), CubeDataType.STRING);
    }

    @Test
    public void testParseCubeFromQueryWithAliases() {
        CubeDescription cubeDescription = sqlCubeDescriptionParser.parseCubeFromQuery(QUERY_WITH_ALIASES, "cubeName");
        assertTrue(cubeDescription.getAgregates().containsAll(asList("total_count", "sum_amount")));
        assertEquals(cubeDescription.getAgregates().size(), 2);
        assertTrue(cubeDescription.getDimensions().containsAll(asList("merchant_login", "hist_state", "action_code")));
        assertEquals(cubeDescription.getDimensions().size(), 3);
    }

}