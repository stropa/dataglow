package ru.rbs.stats.data;

import java.time.LocalDateTime;

public interface StatsReportBuilder {

    //List<MerchantReportEntry> report(LocalDateTime periodStart, LocalDateTime periodEnd);

    Report buildReport(ReportParams config, LocalDateTime periodStart, LocalDateTime periodEnd);
}
