package ru.rbs.stats.data.series.time;

public class Point {

    long timestamp;
    double value;

    public Point(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }


    public double getValue() {
        return value;
    }

}
