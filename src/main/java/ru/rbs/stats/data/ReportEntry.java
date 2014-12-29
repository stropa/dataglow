package ru.rbs.stats.data;

import java.util.List;

public class ReportEntry {

    private List profile;
    private List<Number> aggregates;

    public ReportEntry() {}


    public List getProfile() {
        return profile;
    }

    public void setProfile(List profile) {
        this.profile = profile;
    }

    public List<Number> getAggregates() {
        return aggregates;
    }

    public void setAggregates(List<Number> aggregates) {
        this.aggregates = aggregates;
    }
}
