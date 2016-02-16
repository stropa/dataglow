package org.dataglow.v1.data;

import org.dataglow.v1.data.series.time.Point;
import org.dataglow.v1.store.CubeCoordinates;
import org.dataglow.v1.store.CubeDescription;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class InfluxDBCubeDataSource implements TimedCubeDataSource {

    private static final Logger logger = LoggerFactory.getLogger(InfluxDBCubeDataSource.class);

    private InfluxDB influxDB;
    private String databaseName;

    public InfluxDBCubeDataSource(InfluxDB influxDB, String databaseName) {
        this.influxDB = influxDB;
        this.databaseName = databaseName;
    }

    @Override
    public List<Point> fetch(CubeCoordinates coordinates, LocalDateTime periodStart, LocalDateTime periodEnd, CubeDescription cubeDescription) {
        QueryResult queryResult = influxDB.query(buildQuery(coordinates, periodStart, periodEnd, cubeDescription, databaseName), TimeUnit.MILLISECONDS);
        return toPoints(queryResult, coordinates);
    }

    public void sendToStorage(Report report, LocalDateTime time) {
        BatchPoints batchPoints = BatchPoints.database(databaseName).build();

        List<String> allColumns = new ArrayList<>();
        allColumns.addAll(report.getCubeDescription().getDimensions());
        allColumns.addAll(report.getCubeDescription().getAggregates());
        //allColumns.add("time");

        Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
        Date timeSoSend = Date.from(instant);
        for (ReportEntry entry : report.getEntries()) {
            Map<String, Object> fields = new HashMap<>();
            int i = 0;
            for (String fieldName : allColumns) {
                fields.put(fieldName, i < entry.getProfile().size() ? entry.getProfile().get(i) : entry.getAggregates().get(i - entry.getProfile().size()));
                i++;
            }

            org.influxdb.dto.Point point =
                    org.influxdb.dto.Point.measurement(report.getCubeDescription().getName()).fields(fields)
                            .time(timeSoSend.getTime(), TimeUnit.MILLISECONDS)
                            .build();
            batchPoints.point(point);
        }

        influxDB.write(batchPoints);
        logger.info("A serie " + report.getCubeDescription().getName() + " of " + batchPoints.getPoints().size() + " entries was saved");
    }

    private List<Point> toPoints(QueryResult queryResult, CubeCoordinates coordinates) {
        List<QueryResult.Series> series = queryResult.getResults().iterator().next().getSeries();
        if (series == null) return Collections.emptyList();
        ArrayList<Point> points = new ArrayList<Point>(series.size());
        for (QueryResult.Series serie : series) {
            /*List<Map<String, Object>> rows = serie.
            for (Map<String, Object> row : rows) {
                Object var = row.get(coordinates.getVarName());
                float value = ((Double) var).floatValue();
                long timestamp = ((Double) row.get("time")).longValue();
                LocalDateTime time = LocalDateTime.from(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()));
                Point point = new Point(time, value);
                points.add(point);
            }*/
            List<List<Object>> entries = serie.getValues();
            for (List<Object> values : entries) {
                long timestamp = ((Double) values.get(0)).longValue();
                LocalDateTime time = LocalDateTime.from(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()));
                Point point = new Point(time, ((Double) values.get(1)).floatValue() );
                points.add(point);
            }

        }
        return points;
    }

    // TODO: add query cache
    private Query buildQuery(CubeCoordinates coordinates, LocalDateTime periodStart, LocalDateTime periodEnd,
                             CubeDescription cubeDescription, String dbName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuilder axisPart = new StringBuilder();
        Iterator<String> iterator = coordinates.getProfile().keySet().iterator();
        while (iterator.hasNext()) {
            String axisName = iterator.next();
            axisPart.append(axisName).append(" = ");
            boolean isStringType = cubeDescription.getType(axisName) == CubeDescription.CubeDataType.STRING;
            if (isStringType) axisPart.append("'");
            axisPart.append(coordinates.getProfile().get(axisName));
            if (isStringType) axisPart.append("'");
            axisPart.append(" ");
            if (iterator.hasNext()) axisPart.append("and ");
        }
        axisPart.append(" and time > '").append(periodStart.format(formatter)).append("' and time < '")
                .append(periodEnd.format(formatter)).append("'");

        String query = "select " + coordinates.getVarName() + " from " + coordinates.getCubeName() + " where " + axisPart;
        logger.debug(query);
        return new Query(query, dbName);
    }


    public InfluxDB getInfluxDB() {
        return influxDB;
    }

    public void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
