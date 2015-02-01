package ru.rbs.stats.web.controller.data;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rbs.stats.Stats;
import ru.rbs.stats.data.CachedSerieDataProvider;
import ru.rbs.stats.data.series.time.Point;
import ru.rbs.stats.model.tables.daos.ArtifactDao;
import ru.rbs.stats.model.tables.pojos.Artifact;
import ru.rbs.stats.utils.CompositeName;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController {

    @Resource
    private Stats stats;

    @Resource
    private ArtifactDao artifactDao;

    @RequestMapping("/randomSerie")
    public String randomSerie() {
        return "[{'timestamp': 1422497975, 'value': 10}, { 'timestamp': 1422501975, 'value': 15}, { 'timestamp': 1422550975, 'value': 20}]";
    }

    @RequestMapping("forArtifact/{id}")
    public List<Point> forArtifact(@PathVariable String id) {
        Artifact artifact = artifactDao.findById(Long.valueOf(id));
        CompositeName key = Stats.buildSerieName(artifact.getCubeCoordinates());
        CachedSerieDataProvider provider = new CachedSerieDataProvider(stats.getCachedSeries(), key);
        LocalDateTime periodStart = artifact.getTimeStart().toLocalDateTime();
        List<Point> points = provider.get(periodStart.minusMinutes(30), periodStart.plusMinutes(30));
        return points;
    }

}
