package ru.rbs.stats.data;

import java.time.LocalDateTime;

public interface StatsReportBuilder {

    Report buildReport(LocalDateTime periodStart, LocalDateTime periodEnd);

    ReportParams getConfig();
}
