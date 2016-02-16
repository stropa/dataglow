package org.dataglow.v1.data.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private Map<String, TimeSeriesCache> cachedSeries = new HashMap<>(); // caches by cube names

    public TimeSeriesCache getCache(String name) {
        TimeSeriesCache timeSeriesCache = cachedSeries.get(name);
        if (timeSeriesCache == null) {
            timeSeriesCache = new TimeSeriesCache(name);
            cachedSeries.put(name, timeSeriesCache);
        }
        return timeSeriesCache;
    }

    public Collection<String> getCacheNames() {
        return cachedSeries.keySet();
    }

}
