package org.dataglow.v1.data.cache;

import org.dataglow.v1.utils.CompositeName;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class TimeSeriesCache {

    private String cacheName;
    private AtomicLong updateCounter = new AtomicLong(0);

    private Map<CompositeName, CachedSerieContainer> cachedSeries = new HashMap<>();

    public TimeSeriesCache(String cacheName) {
        this.cacheName = cacheName;
    }

    public long incrementUpdateCounter() {
        return updateCounter.incrementAndGet();
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Map<CompositeName, CachedSerieContainer> getCachedSeries() {
        return cachedSeries;
    }

    public void setCachedSeries(Map<CompositeName, CachedSerieContainer> cachedSeries) {
        this.cachedSeries = cachedSeries;
    }
}
