package ru.rbs.stats.data.series.time;

import java.time.LocalDateTime;

public class Point {

    LocalDateTime timestamp;
    double value;

    public Point(LocalDateTime timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    public double getValue() {
        return value;
    }

}
