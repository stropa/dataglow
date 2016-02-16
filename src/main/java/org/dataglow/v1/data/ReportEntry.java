package org.dataglow.v1.data;

import java.util.ArrayList;
import java.util.List;

public class ReportEntry {

    private List profile = new ArrayList();
    private List<Number> aggregates = new ArrayList<Number>();

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
