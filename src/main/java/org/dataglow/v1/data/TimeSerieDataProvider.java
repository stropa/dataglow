package org.dataglow.v1.data;

import org.dataglow.v1.data.series.time.Point;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSerieDataProvider {

    List<Point> get(LocalDateTime periodStart, LocalDateTime periodEnd);

    String getSerieName();

}
