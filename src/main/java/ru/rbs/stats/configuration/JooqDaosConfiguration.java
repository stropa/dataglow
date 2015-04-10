package ru.rbs.stats.configuration;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rbs.stats.model.tables.daos.ArtifactDao;
import ru.rbs.stats.model.tables.daos.CubeDescriptionDao;
import ru.rbs.stats.model.tables.daos.ReportConfigDao;
import ru.rbs.stats.model.tables.daos.UserAccountDao;

import javax.annotation.Resource;

@Configuration
public class JooqDaosConfiguration {

    @Resource
    private DSLContext dslContext;

    @Bean
    public UserAccountDao getUserAccountDao() {
        return new UserAccountDao(dslContext.configuration());
    }

    @Bean
    public ArtifactDao getArtifactDao() {
        return new ArtifactDao(dslContext.configuration());
    }

    @Bean
    public ReportConfigDao getReportConfigDao() {
        return new ReportConfigDao(dslContext.configuration());
    }

    @Bean
    public CubeDescriptionDao getCubeDescriptionDao() {
        return new CubeDescriptionDao(dslContext.configuration());
    }

}
