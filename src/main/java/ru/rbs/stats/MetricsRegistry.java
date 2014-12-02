package ru.rbs.stats;

import ru.rbs.stats.analyze.TimeSeriesAnalyzeConfig;
import ru.rbs.stats.store.CubeDescription;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetricsRegistry {

    private Map<String, CubeDescription> cubes = new HashMap<String, CubeDescription>();
    private Map<String, TimeSeriesAnalyzeConfig> analyzers = new HashMap<String, TimeSeriesAnalyzeConfig>();


    public void addSeriesAnalyzer(TimeSeriesAnalyzeConfig config) {
        analyzers.put(config.getName(), config);
    }

    public void addCubeDescription(CubeDescription cubeDescription) {
        cubes.put(cubeDescription.getName(), cubeDescription);
    }

    public Collection<TimeSeriesAnalyzeConfig> getAnalyzers() {
        return analyzers.values();
    }

    public Map<String, CubeDescription> getCubes() {
        return cubes;
    }
}
