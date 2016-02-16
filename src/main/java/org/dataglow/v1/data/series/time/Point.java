package org.dataglow.v1.data.series.time;

import org.dataglow.v1.utils.DateUtil;

import java.time.LocalDateTime;

public class Point {

    LocalDateTime localDateTime;
    long timestamp;
    double value;

    public Point(LocalDateTime localDateTime, float value) {
        this.localDateTime = localDateTime;
        this.timestamp = DateUtil.getMillis(localDateTime);
        this.value = value;
    }

    public LocalDateTime getDateTime() {
        return localDateTime;
    }

    public long getTimestamp() {
        return timestamp;
    }


    public double getValue() {
        return value;
    }

}
