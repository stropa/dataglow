package ru.rbs.stats.data;

import ru.rbs.stats.data.cache.CachedSerieContainer;
import ru.rbs.stats.data.series.time.Point;
import ru.rbs.stats.utils.CompositeName;

import java.time.LocalDateTime;
import java.util.*;

public class CachedSerieDataProvider implements TimeSerieDataProvider {

    private Map<CompositeName, CachedSerieContainer> cachedSeries;

    private CompositeName serieName;

    public CachedSerieDataProvider(Map<CompositeName, CachedSerieContainer> cachedSeries, CompositeName serieName) {
        this.cachedSeries = cachedSeries;
        this.serieName = serieName;
    }

    public CachedSerieDataProvider(Map<CompositeName, CachedSerieContainer> cachedSeries) {
        this.cachedSeries = cachedSeries;
    }

    @Override
    public List<Point> get(LocalDateTime periodStart, LocalDateTime periodEnd) {
        List<Point> result = new ArrayList<Point>();
        NavigableMap<LocalDateTime, Number> serie = cachedSeries.get(serieName).getSerie();
        SortedMap<LocalDateTime, Number> subMap = serie.subMap(periodStart, true, periodEnd, true);
        for (LocalDateTime selected : subMap.keySet()) {
            result.add(new Point(selected, subMap.get(selected).floatValue()));
        }
        return result;
    }


    public Map<CompositeName, CachedSerieContainer> getCachedSeries() {
        return cachedSeries;
    }

    public void setCachedSeries(Map<CompositeName, CachedSerieContainer> cachedSeries) {
        this.cachedSeries = cachedSeries;
    }

    public String getSerieName() {
        return serieName.format();
    }

    public void setSerieName(CompositeName serieName) {
        this.serieName = serieName;
    }
}
