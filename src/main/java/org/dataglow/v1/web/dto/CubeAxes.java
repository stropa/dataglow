package org.dataglow.v1.web.dto;

import java.util.List;
import java.util.Map;

public class CubeAxes {
    private Map<String, List<String>> axes;

    public CubeAxes(Map<String, List<String>> axes) {
        this.axes = axes;
    }

    public Map<String, List<String>> getAxes() {
        return axes;
    }

    public void setAxes(Map<String, List<String>> axes) {
        this.axes = axes;
    }
}
