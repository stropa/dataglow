package ru.rbs.stats.data;

public class ReportEntry {

    private Object[] profile;
    private Number[] aggregates;


    public ReportEntry(Object[] profile, Number[] aggregates) {
        this.profile = profile;
        this.aggregates = aggregates;
    }



    public Object[] getProfile() {
        return profile;
    }

    public void setProfile(Object[] profile) {
        this.profile = profile;
    }

    public Number[] getAggregates() {
        return aggregates;
    }

    public void setAggregates(Number[] aggregates) {
        this.aggregates = aggregates;
    }
}
