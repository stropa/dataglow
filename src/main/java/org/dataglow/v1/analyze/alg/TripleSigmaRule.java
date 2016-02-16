package org.dataglow.v1.analyze.alg;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.dataglow.v1.analyze.Artifact;
import org.dataglow.v1.data.TimeSerieDataProvider;
import org.dataglow.v1.data.series.time.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dataglow.v1.analyze.Algorithm;

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
    public List<Artifact> apply(TimeSerieDataProvider dataProvider,
                                LocalDateTime periodStart, LocalDateTime periodEnd) {

        List<Artifact> results = new ArrayList<Artifact>();
        List<Point> points = dataProvider.get(periodStart, periodEnd);
        long seconds = Duration.between(periodStart, periodEnd).getSeconds();
        List<Point> wider = dataProvider.get(periodStart.minusSeconds(seconds * span), periodEnd);

        SummaryStatistics stats = new SummaryStatistics();
        for (Point point : wider) {
            stats.addValue(point.getValue());
        }
        double stdev = stats.getStandardDeviation();
        double mean = stats.getMean();
        logger.trace("Mean: " + mean + " stdev: " + stdev);
        for (Point point : points) {
            if (Math.abs(point.getValue() - mean) > stdev * 3) {
                results.add(new Artifact(point.getDateTime(), "3sigm"));
            }
        }

        return results;
    }
}
