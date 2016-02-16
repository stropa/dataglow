package org.dataglow.v1.store;

public interface CubeSchemaProvider {

    CubeDescription getCubeSchema(String cubeName);

}
