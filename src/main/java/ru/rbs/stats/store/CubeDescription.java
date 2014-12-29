package ru.rbs.stats.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CubeDescription {
    private String name;                                        // like "merchant_daily_counts"
    private List<String> dimensions = new ArrayList<String>();  // like "merchant", "operation", "result_code"
    private List<String> agregates = new ArrayList<String>();   // like "total_amount" "count"

    public CubeDescription(String name) {
        this.name = name;
    }

    private Map<String, CubeDataType> types = new HashMap<String, CubeDataType>();

    public CubeDataType getType(String dimensionOrVar) {
        CubeDataType type = types.get(dimensionOrVar);
        if (type == null) return CubeDataType.STRING;
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions) {
        this.dimensions = dimensions;
    }

    public List<String> getAgregates() {
        return agregates;
    }

    public void setAgregates(List<String> agregates) {
        this.agregates = agregates;
    }

    public Map<String, CubeDataType> getTypes() {
        return types;
    }

    public void setTypes(Map<String, CubeDataType> types) {
        this.types = types;
    }

    public static enum CubeDataType {
        INTEGER, STRING, FLOAT;
    }

}
