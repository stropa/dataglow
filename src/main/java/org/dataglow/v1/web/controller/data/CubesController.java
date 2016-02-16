package org.dataglow.v1.web.controller.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.dataglow.v1.Stats;
import org.dataglow.v1.configuration.StatsReportSchedulingConfiguration;
import org.dataglow.v1.service.CubesService;
import org.dataglow.v1.store.CubeDescription;
import org.dataglow.v1.web.dto.CubeAxes;

import javax.annotation.Resource;
import java.util.Collection;

@RestController
@RequestMapping(value = "/cubes", produces = "application/json")
public class CubesController {

    private static final Logger logger = LoggerFactory.getLogger(CubesController.class);

    @Resource
    private Stats stats;

    @Resource
    private CubesService cubesService;

    @Resource
    private StatsReportSchedulingConfiguration reportSchedulingConfiguration;

    @RequestMapping(method = RequestMethod.GET)
    public Collection<CubeDescription> getAllCubes() {
        //return stats.getMetricsRegistry().getCubes().values();
        return cubesService.getAllCubes();

    }

    @RequestMapping(method = RequestMethod.GET, value = "{id}", produces = "application/json")
    @ResponseBody
    public CubeDescription getById(@PathVariable String id) {
        return cubesService.get(Long.valueOf(id));
    }

    @RequestMapping(method = RequestMethod.GET, value = "{id}/axes" , produces = "application/json")
    @ResponseBody
    public CubeAxes getCubeAxes(@PathVariable String id) {
        CubeAxes cubeAxes = new CubeAxes(cubesService.getAxes(Long.valueOf(id)));
        return cubeAxes;
    }


}
