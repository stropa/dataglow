package org.dataglow.v1.configuration;

import org.dataglow.v1.model.tables.daos.ArtifactDao;
import org.dataglow.v1.model.tables.daos.CubeDescriptionDao;
import org.dataglow.v1.model.tables.daos.ReportConfigDao;
import org.dataglow.v1.model.tables.daos.UserAccountDao;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
