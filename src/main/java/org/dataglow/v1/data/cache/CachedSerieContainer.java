package org.dataglow.v1.data.cache;

import org.dataglow.v1.utils.CompositeName;

import java.time.LocalDateTime;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class CachedSerieContainer {

    CompositeName compositeName;
    String serieName;
    AtomicLong counter = new AtomicLong(0);
    long maxSize = -1;

    NavigableMap<LocalDateTime, Number> serie = new TreeMap<LocalDateTime, Number>();


    public CachedSerieContainer(CompositeName compositeName, long maxSize) {
        this.compositeName = compositeName;
        this.serieName = compositeName.format();
        this.maxSize = maxSize;
    }

    public String getSerieName() {
        return serieName;
    }

    public void setSerieName(String serieName) {
        this.serieName = serieName;
    }

    public NavigableMap<LocalDateTime, Number> getSerie() {
        return serie;
    }

    public void setSerie(NavigableMap<LocalDateTime, Number> serie) {
        this.serie = serie;
    }

    public void putValue(LocalDateTime dateTime, Number val) {
        if (maxSize > 0 && counter.get() >= maxSize) {
            serie.pollFirstEntry();
            counter.decrementAndGet();
        }
        serie.put(dateTime, val);
        counter.incrementAndGet();
    }

    public long getUpdateCount() {
        return counter.get();
    }

    public CompositeName getCompositeName() {
        return compositeName;
    }
}
