package ru.rbs.stats.store;

public interface CubeSchemaProvider {

    CubeDescription getCubeSchema(String cubeName);

}
