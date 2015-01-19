package ru.rbs.stats.data;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.rbs.stats.configuration.SystemProperties;
import ru.rbs.stats.store.CubeDescription;
import schemacrawler.schema.*;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.*;
import static ru.rbs.stats.configuration.SystemProperties.SCHEMA;

@Service
public class SQLCubeDescriptionParser {

    private static final Logger logger = LoggerFactory.getLogger(SQLCubeDescriptionParser.class);

    @Resource
    private DataSource dataSource;

    @Resource(name = "systemProperties")
    private SystemProperties systemProperties;

    private String schemaName;

    private Catalog catalog;

    @PostConstruct
    public void readCatalog() throws Exception {
        SchemaCrawlerOptions options = new SchemaCrawlerOptions();
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
        //options.setTableNamePattern("bpc*");
        final Connection connection = dataSource.getConnection();
        schemaName = systemProperties.getProperty(SCHEMA);
        options.setSchemaInclusionRule(new InclusionRule() {
            @Override
            public boolean test(String s) {
                return StringUtils.equalsIgnoreCase(s, schemaName);
            }
        });
        options.setTableInclusionRule(new InclusionRule() {
            @Override
            public boolean test(String s) {
                boolean b = StringUtils.containsIgnoreCase(s, "bpc");
                logger.debug("Now on table: " + s + "will retrieve: " + b);
                return b;
            }
        });
        options.setRoutineTypes(Collections.<RoutineType>emptyList());
        catalog = SchemaCrawlerUtility.getCatalog(connection, options);
    }

    public CubeDescription parseCubeFromQuery(String query, String name) {

        CubeDescription cubeDescription = new CubeDescription(name);
        String columnsPart = query.substring(indexOfIgnoreCase(query, "select") + "select".length(),
                indexOfIgnoreCase(query, "from"));
        String[] columns = columnsPart.split(",");
        List<String> dimensions = new ArrayList<String>();
        List<String> aggregates = new ArrayList<String>();
        //Pattern p = Pattern.compile("");
        for (String column : columns) {
            String simpleName = column;
            String tableColumnName = column;
            //Matcher m = p.matcher(column);
            if (containsIgnoreCase(simpleName, " as")) {
                simpleName = simpleName.substring(indexOfIgnoreCase(column, " as") + " as".length()).trim();
                tableColumnName = tableColumnName.substring(0, indexOfIgnoreCase(tableColumnName, " as"));
            } else {
                simpleName = simpleName.replace('(', '_');
                simpleName = simpleName.replace(")", "");
                simpleName = simpleName.replace("*", "all");
                simpleName = simpleName.replace('.', '_');
                simpleName = simpleName.replace(" ", "");
            }

            tableColumnName = tableColumnName.replace(" ", "");
            if (column.contains("(")) {
                aggregates.add(simpleName);
            } else {
                dimensions.add(simpleName);
            }
            cubeDescription.getTypes().put(simpleName, selectType(tableColumnName, query));
        }

        cubeDescription.setDimensions(dimensions);
        cubeDescription.setAgregates(aggregates);

        return cubeDescription;

    }

    CubeDescription.CubeDataType selectType(String columnName, String query) {
        try {
            if (columnName.contains("count(")) {
                return CubeDescription.CubeDataType.INTEGER;
            } else if (columnName.contains("(")) {
                columnName = substringBetween(columnName, "(", ")");
            }

            String tableName = getTableNameForColumn(columnName, query);

            Schema schema = catalog.getSchema(schemaName);
            // TODO: handle name cases and quotation for different database vendors
            Table table = getTable(tableName, schema);
            Column column = getColumn(substringAfter(columnName, "."), table);

            String name = column.getColumnDataType().getName().toUpperCase();
            // TODO: discover types correctly
            if (column.getColumnDataType().getJavaSqlType().getJavaSqlTypeGroup() == JavaSqlType.JavaSqlTypeGroup.integer
                    || asList("NUMBER", "BIGINT", "INTEGER", "SMALLINT", "SMALLSERIAL", "BIGSERIAL", "SERIAL").contains(name)) {
                return CubeDescription.CubeDataType.INTEGER;
            } else if (asList("DECIMAL", "NUMERIC", "REAL", "DOUBLE PRECISION").contains(name)) {
                return CubeDescription.CubeDataType.FLOAT;
            } else {
                return CubeDescription.CubeDataType.STRING;
            }


            // a bug in schemacrawler - for NUMBER oracle type "bit" JavaSqlType is returned
            /*JavaSqlTypeGroup typeGroup = column.getColumnDataType().getJavaSqlType().getJavaSqlTypeGroup();
            switch (typeGroup) {
                case integer:
                    return CubeDescription.CubeDataType.INTEGER;
                case real:
                    return CubeDescription.CubeDataType.FLOAT;
                case character:
                    return CubeDescription.CubeDataType.STRING;

            }*/

        } catch (Exception e) {
            logger.error("Can't discover column type for field " + columnName, e);
        }
        return CubeDescription.CubeDataType.STRING;
    }

    private Column getColumn(String columnName, Table table) {
        Column column = table.getColumn(columnName);
        if (column == null) {
            column = table.getColumn(columnName.toUpperCase());
        }
        return column;
    }

    private Table getTable(String tableName, Schema schema) {
        Table table = catalog.getTable(schema, tableName);
        if (table == null) {
            table = catalog.getTable(schema, tableName.toUpperCase());
        }
        return table;
    }

    String getTableNameForColumn(String column, String query) {
        if (column.equals("count(*)")) {
            return "*";
        }
        if (column.contains(".")) {
            String tablePrefix = substringBefore(column, ".").trim();
            String regex = "(from.*?)(\\w+)\\s+(" + tablePrefix + "\\b)";
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(query);
            matcher.find();
            String group = matcher.group(2);
            return substringBefore(group, " ");
        } else throw new NotImplementedException("Not yet implemented for case without a dot");
    }

}
