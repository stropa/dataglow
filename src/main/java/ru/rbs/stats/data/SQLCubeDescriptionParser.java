package ru.rbs.stats.data;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.rbs.stats.configuration.SystemProperties;
import ru.rbs.stats.configuration.options.DatabaseUsed;
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

            if (column.contains("(")) {
                aggregates.add(simpleName);
            } else {
                dimensions.add(simpleName);
            }
        }

        cubeDescription.setDimensions(dimensions);
        cubeDescription.setAggregates(aggregates);

        return cubeDescription;

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
