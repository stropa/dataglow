package ru.rbs.stats.web.controller.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.rbs.stats.configuration.StatsReportSchedulingConfiguration;
import ru.rbs.stats.data.ReportParams;
import ru.rbs.stats.data.SQLCubeDescriptionParser;
import ru.rbs.stats.service.ReportsService;
import ru.rbs.stats.utils.DateUtil;
import ru.rbs.stats.web.dto.Report;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/reports", produces = "application/json" )
public class ReportsController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    public void init() {
        logger.debug("Initializing reports controller");
    }

    @Resource
    private SQLCubeDescriptionParser sqlCubeDescriptionParser;

    @Resource
    private StatsReportSchedulingConfiguration reportSchedulingConfiguration;

    @Resource
    private ReportsService reportsService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Report> getAll() {
        List<Report> reports = new ArrayList<Report>();
        for (ReportParams reportParams : reportSchedulingConfiguration.getReports()) {
            Report report = new Report(
                    reportParams.getReportName(),
                    reportParams.getQuery(),
                    (int) reportParams.getPeriodInUnits(),
                    reportParams.getPeriodUnits());
            report.setLastBuildTime(reportParams.getLastRun() != null ? DateUtil.getMillis(reportParams.getLastRun()) : null);
            reports.add(report);

        }
        return reports;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public void update(@RequestBody Report report) {
        if (report.getId() == null) {
            buildNewReportConfig(report);
        }

    }

    private void buildNewReportConfig(Report report) {
        final ReportParams params = new ReportParams(report.getName(), (long) report.getPeriod(),
                ChronoUnit.valueOf(report.getPeriodUnits()));

        params.setQuery(report.getQuery().replace(";", ""));

        params.setCubeDescription(sqlCubeDescriptionParser.parseCubeFromQuery(report.getQuery(), report.getName().replaceAll("[^\\w]", "")));

        params.setUseCache(report.isUseCache());
        if (report.isUseCache()) {
            params.setCacheAll(report.isCacheAll());
            params.setCacheMask(report.getCacheMask());

            if (report.getMaxCacheAgeUnits() != null && report.getMaxCacheAge() > 0) {
                params.setCacheAge(Duration.of(report.getMaxCacheAge(), ChronoUnit.valueOf(report.getMaxCacheAgeUnits())));
                params.setMaxCacheAgeUnits(report.getMaxCacheAgeUnits());
                params.setMaxCacheAgeInUnits(report.getMaxCacheAge());
            } else {
                //params.setCacheAge(Duration.ofHours(1));
            }
            params.setMaxCacheSize(report.getMaxCacheSize() > 0 ? report.getMaxCacheAge() : -1);
        }
        params.setAnalyzeAll(report.isAnalyzeAll());

        reportsService.startReport(params);
        reportsService.persist(params);

        logger.info("Added new report: " + report);
    }

}
