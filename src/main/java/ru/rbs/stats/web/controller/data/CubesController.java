package ru.rbs.stats.web.controller.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.rbs.stats.Stats;
import ru.rbs.stats.configuration.StatsReportSchedulingConfiguration;
import ru.rbs.stats.service.CubesService;
import ru.rbs.stats.store.CubeDescription;
import ru.rbs.stats.web.dto.CubeAxes;

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
