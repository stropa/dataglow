package ru.rbs.stats.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.rbs.stats.Stats;
import ru.rbs.stats.configuration.StatsReportSchedulingConfiguration;
import ru.rbs.stats.data.ReportParams;
import ru.rbs.stats.data.StatsReportBuilder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
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
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
                                       @RequestParam Boolean cache,
                                       @RequestParam String whatToAnalyze) {

        List<ReportParams> configs = statsReportSchedulingConfiguration.getReports();
        ReportParams reportParams = null;
        for (ReportParams params : configs) {
            if (params.getReportName().equals(report)) reportParams = params;
        }
        if (reportParams == null) return "no such report";
        StatsReportBuilder reportBuilder = stats.getReportBuilders().get(reportParams.getReportName());
        if (reportBuilder == null) return "no builder";

        stats.process(reportParams, false, from, to, cache != null && cache, whatToAnalyze);


        return "done";
    }

}
