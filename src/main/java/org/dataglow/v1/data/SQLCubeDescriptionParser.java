package org.dataglow.v1.data;

import org.apache.commons.lang.NotImplementedException;
import org.dataglow.v1.store.CubeDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.*;

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
