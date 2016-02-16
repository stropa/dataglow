package org.dataglow.v1.data;

import java.time.LocalDateTime;

public interface StatsReportBuilder {

    Report buildReport(LocalDateTime periodStart, LocalDateTime periodEnd);

    ReportParams getConfig();
}
