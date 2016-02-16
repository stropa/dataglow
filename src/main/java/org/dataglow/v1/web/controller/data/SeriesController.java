package org.dataglow.v1.web.controller.data;

import org.dataglow.v1.Stats;
import org.dataglow.v1.data.CachedSerieDataProvider;
import org.dataglow.v1.data.ReportParams;
import org.dataglow.v1.data.TimeSerieDataProvider;
import org.dataglow.v1.data.TimedCubeDataSource;
import org.dataglow.v1.data.series.time.Point;
import org.dataglow.v1.model.tables.daos.ArtifactDao;
import org.dataglow.v1.model.tables.pojos.Artifact;
import org.dataglow.v1.store.CubeCoordinates;
import org.dataglow.v1.store.CubeDescription;
import org.dataglow.v1.utils.CompositeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/series")
public class SeriesController {

    private static final Logger logger = LoggerFactory.getLogger(SeriesController.class);
    public static final DateTimeFormatter FORMATTER = //DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            //DateTimeFormatter.ISO_INSTANT;
    DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Resource
    private Stats stats;

    @Resource
    private ArtifactDao artifactDao;

    @Resource(name = "timedCubeDataSource")
    private TimedCubeDataSource timedCubeDataSource;

    @RequestMapping("/randomSerie")
    public String randomSerie() {
        return "[{'timestamp': 1422497975, 'value': 10}, { 'timestamp': 1422501975, 'value': 15}, { 'timestamp': 1422550975, 'value': 20}]";
    }

    @RequestMapping("forArtifact/{id}")
    public List<Point> forArtifact(@PathVariable String id) {
        Artifact artifact = artifactDao.findById(Long.valueOf(id));
        final CubeCoordinates cubeCoordinates = artifact.getCubeCoordinates();

        String cubeName = cubeCoordinates.getCubeName();
        LocalDateTime periodStart = artifact.getTimeStart().toLocalDateTime();

        return getPoints(cubeCoordinates, cubeName, periodStart, null, true);
    }

    private List<Point> getPoints(final CubeCoordinates cubeCoordinates, String cubeName, LocalDateTime periodStart,
                                  LocalDateTime periodEnd, boolean autoPeriod) {
        CompositeName key = Stats.buildSerieName(cubeCoordinates);
        CubeDescription cubeSchema = stats.getCubeSchema(cubeName);
        ReportParams reportParams = stats.getReportParams(cubeName);

        TimeSerieDataProvider provider = new CachedSerieDataProvider(stats.getCachedSeries(cubeName), key);
        List<Point> points;
        LocalDateTime start = autoPeriod ? periodStart.minusSeconds(reportParams.getPeriodSeconds() * 100) : periodStart;
        LocalDateTime end = periodEnd != null ? periodEnd : periodStart.plusSeconds(reportParams.getPeriodSeconds() * 100);

        points = provider.get(start, end);
        if (points.isEmpty()) {
            provider = new TimeSerieDataProvider() {
                @Override
                public List<Point> get(LocalDateTime periodStart, LocalDateTime periodEnd) {
                    if (cubeSchema == null) {
                        logger.error("Cant find cube description");
                        return Collections.emptyList();
                    }
                    return timedCubeDataSource.fetch(cubeCoordinates, periodStart, periodEnd,
                            cubeSchema);
                }

                @Override
                public String getSerieName() {
                    return null;
                }
            };
            points = provider.get(start, end);
        }
        return points;
    }

    @RequestMapping("forCubeCoordinates")
    public List<Point> forCubeCoordinates(@RequestParam Map<String, String> parameters,
                                          @RequestParam(required = false) String dateFrom,
                                          @RequestParam(required = false) String dateTo,
                                          @RequestParam("cubeName") String cubeName,
                                          @RequestParam(value = "axe0", required = false) String axe0,
                                          @RequestParam(value = "axe1", required = false) String axe1,
                                          @RequestParam(value = "axe2", required = false) String axe2,
                                          @RequestParam(value = "aggregate", required = false) String aggregate) throws ParseException {
        if (aggregate == null) {
            return Collections.emptyList();
        }
        logger.debug(parameters.size() + "");
        CubeDescription cubeSchema = stats.getCubeSchema(cubeName);
        CubeCoordinates cubeCoordinates = new CubeCoordinates(cubeName);
        cubeCoordinates.setVarName(aggregate);
        if (axe0 != null) {
            cubeCoordinates.getProfile().put(cubeSchema.getDimensions().get(0), axe0);
        }
        if (axe1 != null) {
            cubeCoordinates.getProfile().put(cubeSchema.getDimensions().get(1), axe1);
        }
        if (axe2 != null) {
            cubeCoordinates.getProfile().put(cubeSchema.getDimensions().get(2), axe2);
        }

        ReportParams reportParams = stats.getReportParams(cubeName);

        LocalDateTime periodStart = null;
        LocalDateTime periodEnd = null;
        if (dateFrom != null && dateTo != null) {
            periodStart = LocalDateTime.parse(dateFrom, FORMATTER);
            periodEnd = LocalDateTime.parse(dateTo, FORMATTER);
        } else {
            periodStart = LocalDateTime.now().minusSeconds(reportParams.getPeriodSeconds() * 100);
        }

        List<Point> points = getPoints(cubeCoordinates, cubeName, periodStart, periodEnd, false);
        return points;
    }

}
