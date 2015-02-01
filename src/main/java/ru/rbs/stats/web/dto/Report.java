package ru.rbs.stats.web.dto;

public class Report {
    private Long id;
    private String name;
    private String query;
    private int period;
    private String periodUnits;
    private Long lastBuildTime;

    private boolean useCache;
    private boolean cacheAll;
    private String cacheMask;
    private int maxCacheAge;
    private String maxCacheAgeUnits;
    private long maxCacheSize;

    private boolean analyzeAll;


    public Report(String name, String query, int period, String periodUnits) {
        this.name = name;
        this.query = query;
        this.period = period;
        this.periodUnits = periodUnits;
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

    public Long getLastBuildTime() {
        return lastBuildTime;
    }

    public void setLastBuildTime(Long lastBuildTime) {
        this.lastBuildTime = lastBuildTime;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isCacheAll() {
        return cacheAll;
    }

    public void setCacheAll(boolean cacheAll) {
        this.cacheAll = cacheAll;
    }

    public String getCacheMask() {
        return cacheMask;
    }

    public void setCacheMask(String cacheMask) {
        this.cacheMask = cacheMask;
    }

    public int getMaxCacheAge() {
        return maxCacheAge;
    }

    public void setMaxCacheAge(int maxCacheAge) {
        this.maxCacheAge = maxCacheAge;
    }

    public String getMaxCacheAgeUnits() {
        return maxCacheAgeUnits;
    }

    public void setMaxCacheAgeUnits(String maxCacheAgeUnits) {
        this.maxCacheAgeUnits = maxCacheAgeUnits;
    }

    public boolean isAnalyzeAll() {
        return analyzeAll;
    }

    public void setAnalyzeAll(boolean analyzeAll) {
        this.analyzeAll = analyzeAll;
    }

    public long getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
}
