package ru.rbs.stats.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rbs.stats.Stats;
import ru.rbs.stats.configuration.StatsReportSchedulingConfiguration;
import ru.rbs.stats.data.*;
import ru.rbs.stats.data.merchants.ReportEntry;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Controller
@RequestMapping("/historical")
public class BuildHistoricalStatsController {

    private static final Logger logger = LoggerFactory.getLogger(BuildHistoricalStatsController.class);

    @Autowired
    private StatsReportSchedulingConfiguration statsReportSchedulingConfiguration;

    @Autowired
    private Stats stats;

    @RequestMapping(value = "/build", method = RequestMethod.POST)
    public String buildHistoricalStats(@RequestParam String report,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to) {

        List<ReportParams> configs = statsReportSchedulingConfiguration.getReports();
        ReportParams reportParams = null;
        for (ReportParams params : configs) {
            if (params.getReportName().equals(report)) reportParams = params;
        }
        if (reportParams == null) return "no such report";
        StatsReportBuilder reportBuilder = stats.getReportBuilders().get(reportParams.getReportName());
        if (reportBuilder == null) return "no builder";

        /*LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochMilli(fromDate.getTime()), TimeZone.getDefault().toZoneId());
        LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochMilli(toDate.getTime()), TimeZone.getDefault().toZoneId());*/

        LocalDateTime periodStart = from;
        if (to == null) to = now();
        long periodSeconds = reportParams.getPeriodSeconds();
        LocalDateTime periodEnd = from.plusSeconds(periodSeconds);
        // for historical reports periodEnd must be in past more farther than in one period
        int part = 1;
        while (periodEnd.isBefore(to.minusSeconds(periodSeconds))) {
            // build report for next period part
            List<ReportEntry> reportEntries = reportBuilder.report(
                    part > 1 ? periodStart.plusNanos(1000) : periodStart, periodEnd);
            logger.debug("Sending page {} of {} entries", part, reportEntries.size());
            stats.sendToStorage(reportEntries, periodEnd);
            periodEnd = periodEnd.plusSeconds(periodSeconds);
            periodStart = periodStart.plusSeconds(periodSeconds);
            part++;

        }

        return "done";
    }

}
