package ru.rbs.stats.data;

import java.time.LocalDateTime;

public interface StatsReportBuilder {

    Report buildReport(ReportParams config, LocalDateTime periodStart, LocalDateTime periodEnd);
}
