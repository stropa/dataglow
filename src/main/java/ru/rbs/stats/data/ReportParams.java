package ru.rbs.stats.data;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBefore;

public class ReportParams {

    private String reportName;
    private Long periodSeconds;
    private LocalDateTime lastRun;

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

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public void setPeriodSeconds(long periodSeconds) {
        this.periodSeconds = periodSeconds;
    }

    public String getPeriod() {
        return periodSeconds + " MILLISECONDS";
    }

    public LocalDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    public void setPeriod(String periodStr) {
        this.periodSeconds = TimeUnit.SECONDS.convert(Long.parseLong(substringBefore(periodStr, " ")),
                TimeUnit.valueOf(substringAfterLast(periodStr, " ")));
    }
}
