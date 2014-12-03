package ru.rbs.stats.data;

import ru.rbs.stats.store.CubeDescription;

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
