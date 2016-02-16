package org.dataglow.v1.service;

import org.dataglow.v1.configuration.StatsReportSchedulingConfiguration;
import org.dataglow.v1.data.ReportParams;
import org.dataglow.v1.data.merchants.SQLReportBuilder;
import org.dataglow.v1.model.Sequences;
import org.dataglow.v1.model.Tables;
import org.dataglow.v1.model.tables.daos.CubeDescriptionDao;
import org.dataglow.v1.model.tables.pojos.CubeDescription;
import org.dataglow.v1.model.tables.records.ReportConfigRecord;
import org.jooq.DSLContext;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.dataglow.v1.Stats;

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

        CubeDescription cd = cubesService.saveCubeDescription(params.getCubeDescription());
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
