package ru.rbs.stats.analyze.alg;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rbs.stats.analyze.Algorithm;
import ru.rbs.stats.analyze.Artefact;
import ru.rbs.stats.data.TimedCubeDataSource;
import ru.rbs.stats.data.series.time.Point;
import ru.rbs.stats.store.CubeCoordinates;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TripleSigmaRule implements Algorithm {

    private static final Logger logger = LoggerFactory.getLogger(TripleSigmaRule.class);

    private int span;

    public TripleSigmaRule(int span) {
        this.span = span;
    }

    @Override
    public List<Artefact> apply(TimedCubeDataSource dataSource, CubeCoordinates coordinates,
                                LocalDateTime periodStart, LocalDateTime periodEnd) {

        List<Artefact> results = new ArrayList<Artefact>();
        List<Point> points = dataSource.fetch(coordinates, periodStart, periodEnd);
        long seconds = Duration.between(periodStart, periodEnd).getSeconds();
        List<Point> wider = dataSource.fetch(coordinates, periodStart.minusSeconds(seconds * span), periodEnd);

        SummaryStatistics stats = new SummaryStatistics();
        for (Point point : wider) {
            stats.addValue(point.getValue());
        }
        double stdev = stats.getStandardDeviation();
        double mean = stats.getMean();
        logger.debug("Mean: " + mean + " stdev: " + stdev);
        for (Point point : points) {
            if (Math.abs(point.getValue() - mean) > stdev * 3) {
                results.add(new Artefact(point.getTimestamp(), "3sigm"));
            }
        }

        return results;
    }
}
