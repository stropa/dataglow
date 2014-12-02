package ru.rbs.stats.data;

import ru.rbs.stats.data.series.time.Point;
import ru.rbs.stats.store.CubeCoordinates;

import java.time.LocalDateTime;
import java.util.List;

public interface TimedCubeDataSource {

    List<Point> fetch(CubeCoordinates coordinates, LocalDateTime periodStart, LocalDateTime periodEnd);

}
