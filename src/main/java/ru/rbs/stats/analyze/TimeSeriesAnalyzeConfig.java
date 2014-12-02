package ru.rbs.stats.analyze;

import ru.rbs.stats.data.AbstractSchedulableJob;
import ru.rbs.stats.store.CubeCoordinates;

/*
* Binds some metric (a time series) stored within a cube with some coordinates to some algorithm
 * executed for some periodInSeconds of time.
* */
public class TimeSeriesAnalyzeConfig extends AbstractSchedulableJob {

    String name;
    CubeCoordinates coordinates;
    Algorithm algorithm;


    public TimeSeriesAnalyzeConfig(String name, CubeCoordinates coordinates, Algorithm algorithm, long periodInSeconds) {
        this.name = name;
        this.coordinates = coordinates;
        this.algorithm = algorithm;
        this.periodSeconds = periodInSeconds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CubeCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CubeCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }


}
