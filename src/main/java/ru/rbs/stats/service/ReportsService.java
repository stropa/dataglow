package ru.rbs.stats.service;

import org.jooq.DSLContext;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.rbs.stats.Stats;
import ru.rbs.stats.configuration.StatsReportSchedulingConfiguration;
import ru.rbs.stats.data.ReportParams;
import ru.rbs.stats.data.merchants.SQLReportBuilder;
import ru.rbs.stats.model.Sequences;
import ru.rbs.stats.model.Tables;
import ru.rbs.stats.model.tables.daos.CubeDescriptionDao;
import ru.rbs.stats.model.tables.records.ReportConfigRecord;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Iterator;

@Service
public class ReportsService {

    private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private Stats stats;

    @Resource
    private StatsReportSchedulingConfiguration reportSchedulingConfiguration;

    @Resource
    private CubeDescriptionDao cubeDescriptionDao;

    @Resource
    private CubesService cubesService;

    @Resource
    private DSLContext dslContext;

    @Resource
    private ModelMapper modelMapper;

    public void persist(ReportParams params) {

        ru.rbs.stats.model.tables.pojos.CubeDescription cd = cubesService.saveCubeDescription(params.getCubeDescription());
        params.setCubeId(cd.getId());

        if (params.getId() == null) {
            params.setId(dslContext.nextval(Sequences.REPORT_CONFIG_IDS).longValue());
        }

        ReportConfigRecord record = modelMapper.map(params, ReportConfigRecord.class);
        dslContext.insertInto(Tables.REPORT_CONFIG).set(record).execute();
    }



    @PostConstruct
    public void loadReports() {

        Iterator<ReportConfigRecord> iterator = dslContext.selectFrom(Tables.REPORT_CONFIG).fetch().iterator();
        while (iterator.hasNext()) {
            ReportParams reportParams = modelMapper.map(iterator.next(), ReportParams.class);
            reportParams.setCubeDescription(cubesService.get(reportParams.getCubeId()));
            startReport(reportParams);
        }

    }

    public void startReport(ReportParams reportParams) {
        logger.debug("Initializing Report: " + reportParams);
        reportParams.setJob(new Runnable() {
            @Override
            public void run() {
                stats.process(reportParams, false, null, null, true, "*");
            }
        });
        stats.getReportBuilders().put(reportParams.getReportName(), new SQLReportBuilder(jdbcTemplate, reportParams));
        stats.getMetricsRegistry().addCubeDescription(reportParams.getCubeDescription());
        reportSchedulingConfiguration.getReports().add(reportParams);
    }
}
