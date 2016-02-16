package org.dataglow.v1.data;

import org.dataglow.v1.store.CubeDescription;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class ReportParams extends AbstractSchedulableJob {

    Long id, cubeId;

    private String reportName;
    private CubeDescription cubeDescription;
    //private ChronoUnit timeUnit;
    private Long period;
    private String periodUnits;
    private String query;

    private boolean useCache;
    private boolean cacheAll;
    private String cacheMask;
    private Duration cacheAge;
    private long maxCacheSize;
    private boolean analyzeAll;
    private String maxCacheAgeUnits;
    private int maxCacheAgeInUnits;

    public ReportParams() {
    }

    public ReportParams(String reportName, Long period, ChronoUnit periodUnits) {
        this.reportName = reportName;
        Duration duration = Duration.of(period, periodUnits);
        this.period = period;
        this.periodUnits = periodUnits.name();
        //this.timeUnit = periodUnits;
        this.periodSeconds = duration.getSeconds();
    }

    @Override
    public long getPeriodSeconds() {
        return TimeUnit.SECONDS.convert(period, TimeUnit.valueOf(periodUnits));
    }

    public long getPeriodInUnits() {
        return period;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }


    public CubeDescription getCubeDescription() {
        return cubeDescription;
    }

    public void setCubeDescription(CubeDescription cubeDescription) {
        this.cubeDescription = cubeDescription;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCubeId() {
        return cubeId;
    }

    public void setCubeId(Long cubeId) {
        this.cubeId = cubeId;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public String getPeriodUnits() {
        return periodUnits;
    }

    public void setPeriodUnits(String periodUnits) {
        this.periodUnits = periodUnits;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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

    public Duration getCacheAge() {
        return cacheAge;
    }

    public void setCacheAge(Duration cacheAge) {
        this.cacheAge = cacheAge;
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

    public void setMaxCacheAgeUnits(String maxCacheAgeUnits) {
        this.maxCacheAgeUnits = maxCacheAgeUnits;
    }

    public String getMaxCacheAgeUnits() {
        return maxCacheAgeUnits;
    }

    public void setMaxCacheAgeInUnits(int maxCacheAgeInUnits) {
        this.maxCacheAgeInUnits = maxCacheAgeInUnits;
    }

    public int getMaxCacheAgeInUnits() {
        return maxCacheAgeInUnits;
    }
}
