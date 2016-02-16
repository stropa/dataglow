package org.dataglow.v1.service;

import org.dataglow.v1.analyze.Artifact;
import org.dataglow.v1.model.Sequences;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.dataglow.v1.model.tables.daos.ArtifactDao;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ArtifactsService {

    @Resource
    ArtifactDao artifactDao;

    @Resource
    private DSLContext dslContext;

    public void persist(Artifact artifact) {
        Number nextval = dslContext.nextval(Sequences.ARTIFACT_IDS);
        artifactDao.insert(new org.dataglow.v1.model.tables.pojos.Artifact(nextval.longValue(),
                artifact.getName(),
                artifact.getDescription(), java.sql.Timestamp.valueOf(artifact.getTimestamp()), null,
                artifact.getCoordinates()));
    }


    public List<org.dataglow.v1.model.tables.pojos.Artifact> listAll() {
        return artifactDao.findAll();
    }
}
