package ru.rbs.stats.data;

import ru.rbs.stats.data.merchants.ReportEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsReportBuilder {
    List<ReportEntry> report(LocalDateTime periodStart, LocalDateTime periodEnd);
}
