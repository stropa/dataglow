package org.dataglow.v1.data;

import org.dataglow.v1.store.CubeDescription;

import java.util.ArrayList;
import java.util.List;

public class Report {

    private CubeDescription cubeDescription;

    private List<ReportEntry> entries = new ArrayList<ReportEntry>();


    public CubeDescription getCubeDescription() {
        return cubeDescription;
    }

    public void setCubeDescription(CubeDescription cubeDescription) {
        this.cubeDescription = cubeDescription;
    }

    public List<ReportEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ReportEntry> entries) {
        this.entries = entries;
    }
}
