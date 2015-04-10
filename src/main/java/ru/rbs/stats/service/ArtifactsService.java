package ru.rbs.stats.service;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.rbs.stats.analyze.Artifact;
import ru.rbs.stats.model.Sequences;
import ru.rbs.stats.model.tables.daos.ArtifactDao;

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
        artifactDao.insert(new ru.rbs.stats.model.tables.pojos.Artifact(nextval.longValue(),
                artifact.getName(),
                artifact.getDescription(), java.sql.Timestamp.valueOf(artifact.getTimestamp()), null,
                artifact.getCoordinates()));
    }


    public List<ru.rbs.stats.model.tables.pojos.Artifact> listAll() {
        return artifactDao.findAll();
    }
}
