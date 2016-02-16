package org.dataglow.v1.analyze;

import org.dataglow.v1.data.TimeSerieDataProvider;

import java.time.LocalDateTime;
import java.util.List;

public interface Algorithm {
    /*List<Artifact> apply(TimedCubeDataSource dataSource, CubeCoordinates coordinates,
                         LocalDateTime periodStart, LocalDateTime periodEnd);*/

    List<Artifact> apply(TimeSerieDataProvider dataProvider, LocalDateTime periodStart, LocalDateTime periodEnd);
}
