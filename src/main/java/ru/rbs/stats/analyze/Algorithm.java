package ru.rbs.stats.analyze;

import ru.rbs.stats.data.TimedCubeDataSource;
import ru.rbs.stats.store.CubeCoordinates;

import java.time.LocalDateTime;
import java.util.List;

public interface Algorithm {
    List<Artefact> apply(TimedCubeDataSource dataSource, CubeCoordinates coordinates,
                         LocalDateTime periodStart, LocalDateTime periodEnd);
}
