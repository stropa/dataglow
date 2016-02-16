package org.dataglow.v1.web.controller;

import org.springframework.web.bind.annotation.*;
import org.dataglow.v1.service.ArtifactsService;
import org.dataglow.v1.store.CubeCoordinates;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/artifacts")
public class ArtifactsController {

    @Resource
    private ArtifactsService service;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<org.dataglow.v1.model.tables.pojos.Artifact> listAll() {
        return service.listAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public String addTestOne() {
        org.dataglow.v1.analyze.Artifact artifact = new org.dataglow.v1.analyze.Artifact(LocalDateTime.now(), "first stored artifact");
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
