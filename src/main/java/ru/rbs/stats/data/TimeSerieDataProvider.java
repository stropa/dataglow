package ru.rbs.stats.data;

import ru.rbs.stats.data.series.time.Point;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSerieDataProvider {

    List<Point> get(LocalDateTime periodStart, LocalDateTime periodEnd);

}
