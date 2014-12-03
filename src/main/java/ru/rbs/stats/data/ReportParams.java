package ru.rbs.stats.data;

import ru.rbs.stats.store.CubeDescription;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBefore;

public class ReportParams extends AbstractSchedulableJob {

    private String reportName;
    private CubeDescription cubeDescription;

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

    public String getPeriod() {
        return periodSeconds + " MILLISECONDS";
    }

    public void setPeriod(String periodStr) {
        this.periodSeconds = TimeUnit.SECONDS.convert(Long.parseLong(substringBefore(periodStr, " ")),
                TimeUnit.valueOf(substringAfterLast(periodStr, " ")));
    }

    public CubeDescription getCubeDescription() {
        return cubeDescription;
    }

    public void setCubeDescription(CubeDescription cubeDescription) {
        this.cubeDescription = cubeDescription;
    }
}
