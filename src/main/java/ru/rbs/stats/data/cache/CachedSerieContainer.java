package ru.rbs.stats.data.cache;

import java.time.LocalDateTime;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class CachedSerieContainer {

    String serieName;
    AtomicLong counter;
    long maxSize = -1;

    NavigableMap<LocalDateTime, Number> serie = new TreeMap<LocalDateTime, Number>();


    public CachedSerieContainer(String serieName, long maxSize) {
        this.serieName = serieName;
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
        }
        serie.put(dateTime, val);
    }

}
