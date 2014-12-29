package ru.rbs.stats.data;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.rbs.stats.store.CubeDescription;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.*;
import static schemacrawler.schema.JavaSqlType.JavaSqlTypeGroup;

@Service
public class SQLCubeDescriptionParser {

    private static final Logger logger = LoggerFactory.getLogger(SQLCubeDescriptionParser.class);

    @Resource
    private DataSource dataSource;

    private Catalog catalog;

    @PostConstruct
    public void readCatalog() throws Exception {
        SchemaCrawlerOptions options = new SchemaCrawlerOptions();
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
        catalog = SchemaCrawlerUtility.getCatalog(dataSource.getConnection(), options);
    }

    public CubeDescription parseCubeFromQuery(String query, String name) {

        CubeDescription cubeDescription = new CubeDescription(name);
        try {
            Connection connection = dataSource.getConnection();


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
                    tableColumnName = tableColumnName.substring(0,indexOfIgnoreCase(tableColumnName, " as"));
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

        } catch (SQLException e) {
            throw new DataSourceException("Failed to read schema info from SQL database");
        }
    }

    CubeDescription.CubeDataType selectType(String columnName, String query) {
        try {
            if (columnName.contains("count(")) {
                return CubeDescription.CubeDataType.INTEGER;
            } else if (columnName.contains("(")) {
                columnName = substringBetween(columnName, "(", ")");
            }

            String tableName = getTableNameForColumn(columnName, query);

            Schema schema = catalog.getSchema("public");
            Table table = catalog.getTable(schema, tableName);
            Column column = table.getColumn(substringAfter(columnName, "."));
            JavaSqlTypeGroup typeGroup = column.getColumnDataType().getJavaSqlType().getJavaSqlTypeGroup();
            switch (typeGroup) {
                case integer:
                    return CubeDescription.CubeDataType.INTEGER;
                case real:
                    return CubeDescription.CubeDataType.FLOAT;
                case character:
                    return CubeDescription.CubeDataType.STRING;

            }

        } catch (Exception e) {
            logger.error("Can't discover column type for field " + columnName, e);
        }
        return CubeDescription.CubeDataType.STRING;
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
