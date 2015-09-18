package ru.rbs.stats.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CubeDescription {
    private Long id;
    private String name;                                        // like "merchant_daily_counts"
    private List<String> dimensions = new ArrayList<String>();  // like "merchant", "operation", "result_code"
    private List<String> aggregates = new ArrayList<String>();   // like "total_amount" "count"
    private Map<String, CubeDataType> types = new HashMap<String, CubeDataType>();



    public CubeDescription(String name) {
        this.name = name;
    }

    public CubeDataType getType(String dimensionOrVar) {
        CubeDataType type = types.get(dimensionOrVar);
        if (type == null) return CubeDataType.STRING;
        return type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getAggregates() {
        return aggregates;
    }

    public void setAggregates(List<String> aggregates) {
        this.aggregates = aggregates;
    }

    public Map<String, CubeDataType> getTypes() {
        return types;
    }

    public void setTypes(Map<String, CubeDataType> types) {
        this.types = types;
    }

    public static enum CubeDataType {
        INTEGER, STRING, FLOAT, UNKNOWN
    }

}
