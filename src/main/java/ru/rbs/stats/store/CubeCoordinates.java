package ru.rbs.stats.store;

import java.util.HashMap;
import java.util.Map;


/* profile must contain values for all dimensions to give coordinates of one point in n-dimension cube
 every point in cube can contain many variables (attributes with names and values)
 cube obtains +1 dimension for time. So, every point in cube becomes a time-series. */
public class CubeCoordinates {

    String cubeName;                    // like "merchant_daily_counts"
    Map<String, Object> profile = new HashMap<>();        // like "merchant" -> "Pet Shop", operation -> "purchase", "result_code"
    String varName;                     // like "total_amount"


    public CubeCoordinates() {
    }

    public void setAxis(String dimension, Object value) {
        if (profile == null) profile = new HashMap<String, Object>();
        profile.put(dimension, value);
    }

    public CubeCoordinates(String cubeName) {
        this.cubeName = cubeName;
    }

    public String getCubeName() {
        return cubeName;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    public Map<String, Object> getProfile() {
        return profile;
    }

    public void setProfile(Map<String, Object> profile) {
        this.profile = profile;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return "CubeCoordinates{" +
                "cubeName='" + cubeName + '\'' +
                ", profile=" + profile +
                ", varName='" + varName + '\'' +
                '}';
    }
}
