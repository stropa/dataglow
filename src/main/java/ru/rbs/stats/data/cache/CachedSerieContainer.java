package ru.rbs.stats.data.cache;

import java.time.LocalDateTime;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CachedSerieContainer {

    String serieName;
    NavigableMap<LocalDateTime, Number> serie = new TreeMap<LocalDateTime, Number>();


    public CachedSerieContainer(String serieName) {
        this.serieName = serieName;
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
        serie.put(dateTime, val);
    }

}
