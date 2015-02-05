package ru.rbs.stats.data;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rbs.stats.data.series.time.Point;
import ru.rbs.stats.store.CubeCoordinates;
import ru.rbs.stats.store.CubeDescription;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        List<Serie> series = influxDB.query(databaseName, buildQuery(coordinates, periodStart, periodEnd, cubeDescription), TimeUnit.MILLISECONDS);
        return toPoints(series, coordinates);
    }

    private List<Point> toPoints(List<Serie> series, CubeCoordinates coordinates) {
        ArrayList<Point> points = new ArrayList<Point>(series.size());
        for (Serie serie : series) {
            List<Map<String, Object>> rows = serie.getRows();
            for (Map<String, Object> row : rows) {
                Object var = row.get(coordinates.getVarName());
                float value = ((Double) var).floatValue();
                long timestamp = ((Double) row.get("time")).longValue();
                LocalDateTime time = LocalDateTime.from(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()));
                Point point = new Point(time, value);
                points.add(point);
            }

        }
        return points;
    }

    // TODO: add query cache
    private String buildQuery(CubeCoordinates coordinates, LocalDateTime periodStart, LocalDateTime periodEnd, CubeDescription cubeDescription) {
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
        return query;
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
