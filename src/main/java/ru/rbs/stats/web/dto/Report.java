package ru.rbs.stats.web.dto;

public class Report {
    private Long id;
    private String name;
    private String query;
    private int period;
    private String periodUnits;
    private long lastBuildTime;


    public Report(String name, String query, int period, String periodUnits, long lastBuildTime) {
        this.name = name;
        this.query = query;
        this.period = period;
        this.periodUnits = periodUnits;
        this.lastBuildTime = lastBuildTime;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getPeriodUnits() {
        return periodUnits;
    }

    public void setPeriodUnits(String periodUnits) {
        this.periodUnits = periodUnits;
    }

    public long getLastBuildTime() {
        return lastBuildTime;
    }

    public void setLastBuildTime(long lastBuildTime) {
        this.lastBuildTime = lastBuildTime;
    }
}