package ru.rbs.stats.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.rbs.stats.model.tables.pojos.Artifact;
import ru.rbs.stats.service.ArtifactsService;
import ru.rbs.stats.store.CubeCoordinates;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/artifacts")
public class ArtifactsController {

    @Resource
    private ArtifactsService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Artifact> listAll() {
        return service.listAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public String addTestOne() {
        ru.rbs.stats.analyze.Artifact artifact = new ru.rbs.stats.analyze.Artifact(LocalDateTime.now(), "first stored artifact");
        CubeCoordinates cube = new CubeCoordinates("test_cube");
        cube.setAxis("dimA", "valA");
        cube.setAxis("dimB", "valB");
        cube.setAxis("dimC", "valC");
        cube.setVarName("someVar");
        artifact.setCoordinates(cube);
        artifact.setName("TestArtifact");
        service.persist(artifact);
        return "OK";
    }

}
