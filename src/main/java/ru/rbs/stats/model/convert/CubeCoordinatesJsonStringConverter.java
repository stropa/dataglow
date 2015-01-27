package ru.rbs.stats.model.convert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jooq.Converter;
import ru.rbs.stats.store.CubeCoordinates;

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
