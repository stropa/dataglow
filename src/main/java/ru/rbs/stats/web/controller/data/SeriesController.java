package ru.rbs.stats.web.controller.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rbs.stats.Stats;
import ru.rbs.stats.data.CachedSerieDataProvider;
import ru.rbs.stats.data.ReportParams;
import ru.rbs.stats.data.TimeSerieDataProvider;
import ru.rbs.stats.data.TimedCubeDataSource;
import ru.rbs.stats.data.series.time.Point;
import ru.rbs.stats.model.tables.daos.ArtifactDao;
import ru.rbs.stats.model.tables.pojos.Artifact;
import ru.rbs.stats.store.CubeDescription;
import ru.rbs.stats.utils.CompositeName;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController {

    private static final Logger logger = LoggerFactory.getLogger(SeriesController.class);

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
        CompositeName key = Stats.buildSerieName(artifact.getCubeCoordinates());
        String cubeName = artifact.getCubeCoordinates().getCubeName();
        CubeDescription cubeSchema = stats.getCubeSchema(cubeName);
        ReportParams reportParams = stats.getReportParams(cubeName);

        TimeSerieDataProvider provider = new CachedSerieDataProvider(stats.getCachedSeries(), key);
        LocalDateTime periodStart = artifact.getTimeStart().toLocalDateTime();
        List<Point> points;
        LocalDateTime start = periodStart.minusSeconds(reportParams.getPeriodSeconds() * 100);
        LocalDateTime end = periodStart.plusSeconds(reportParams.getPeriodSeconds() * 100);

        points = provider.get(start, end);
        if (points.isEmpty()) {
            provider = new TimeSerieDataProvider() {
                @Override
                public List<Point> get(LocalDateTime periodStart, LocalDateTime periodEnd) {
                    if (cubeSchema == null) {
                        logger.error("Cant find cube description");
                        return Collections.emptyList();
                    }
                    return timedCubeDataSource.fetch(artifact.getCubeCoordinates(), periodStart, periodEnd,
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

}
