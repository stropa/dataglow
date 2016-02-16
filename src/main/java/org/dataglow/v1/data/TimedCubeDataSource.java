package org.dataglow.v1.data;

import org.dataglow.v1.data.series.time.Point;
import org.dataglow.v1.store.CubeDescription;
import org.dataglow.v1.store.CubeCoordinates;

import java.time.LocalDateTime;
import java.util.List;

public interface TimedCubeDataSource {

    List<Point> fetch(CubeCoordinates coordinates, LocalDateTime periodStart, LocalDateTime periodEnd, CubeDescription cubeDescription);

    void sendToStorage(Report report, LocalDateTime time);

}
