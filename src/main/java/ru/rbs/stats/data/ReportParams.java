package ru.rbs.stats.data;

import ru.rbs.stats.store.CubeDescription;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBefore;

public class ReportParams extends AbstractSchedulableJob {

    private String reportName;
    private CubeDescription cubeDescription;
    private TimeUnit timeUnit;
    private String sql;

    private boolean useCache;
    private boolean cacheAll;
    private String cacheMask;
    private Duration cacheAge;
    private long maxCacheSize;
    private boolean analyzeAll;


    public ReportParams(String reportName, Long periodSeconds) {
        this.reportName = reportName;
        this.periodSeconds = periodSeconds;
    }

    public ReportParams(String reportName, Long period, ChronoUnit periodUnits) {
        this.reportName = reportName;
        Duration duration = Duration.of(period, periodUnits);
        this.periodSeconds = duration.getSeconds();
    }

    public ReportParams(String reportName, String period) {
        this.reportName = reportName;
        setPeriod(period);
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getPeriodInSecondsFormatted() {
        return periodSeconds + " SECONDS";
    }

    public String getPeriodFormatted() {
        return timeUnit.convert(periodSeconds, TimeUnit.SECONDS) + timeUnit.name();
    }

    public long getPeriodInUnits() {
        return timeUnit.convert(periodSeconds, TimeUnit.SECONDS);
    }

    public void setPeriod(String periodStr) {
        long duration = Long.parseLong(substringBefore(periodStr, " "));
        TimeUnit unit = TimeUnit.valueOf(substringAfterLast(periodStr, " "));
        this.timeUnit = unit;
        this.periodSeconds = TimeUnit.SECONDS.convert(duration,unit);
    }

    public CubeDescription getCubeDescription() {
        return cubeDescription;
    }

    public void setCubeDescription(CubeDescription cubeDescription) {
        this.cubeDescription = cubeDescription;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
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
}
