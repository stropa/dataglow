package ru.rbs.stats.data;

import ru.rbs.stats.store.CubeDescription;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBefore;

public class ReportParams extends AbstractSchedulableJob {

    private String reportName;
    private CubeDescription cubeDescription;
    private TimeUnit timeUnit;
    private String sql;



    public ReportParams(String reportName, Long periodSeconds) {
        this.reportName = reportName;
        this.periodSeconds = periodSeconds;
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
}
