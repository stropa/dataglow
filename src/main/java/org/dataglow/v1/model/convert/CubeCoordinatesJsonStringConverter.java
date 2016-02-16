package org.dataglow.v1.model.convert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dataglow.v1.store.CubeCoordinates;
import org.jooq.Converter;

public class CubeCoordinatesJsonStringConverter implements Converter<String, CubeCoordinates> {


    @Override
    public CubeCoordinates from(String databaseObject) {
        return new Gson().fromJson(databaseObject, CubeCoordinates.class);
    }

    @Override
    public String to(CubeCoordinates userObject) {
        return new GsonBuilder().create().toJson(userObject, CubeCoordinates.class);
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<CubeCoordinates> toType() {
        return CubeCoordinates.class;
    }
}
