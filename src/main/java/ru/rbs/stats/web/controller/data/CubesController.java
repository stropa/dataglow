package ru.rbs.stats.web.controller.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.rbs.stats.Stats;
import ru.rbs.stats.configuration.StatsReportSchedulingConfiguration;
import ru.rbs.stats.service.CubesService;
import ru.rbs.stats.store.CubeDescription;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cubes")
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

    @RequestMapping("{id}")
    public CubeDescription getById(@PathVariable String id) {
        return cubesService.get(Long.valueOf(id));
    }

    @RequestMapping("{id}/axes")
    public CubeAxes getCubeAxes(@PathVariable String id) {
        return new CubeAxes(cubesService.getAxes(Long.valueOf(id)));
    }

    public static class CubeAxes {
        private Map<String, List<String>> axes;

        public CubeAxes(Map<String, List<String>> axes) {
            this.axes = axes;
        }
    }


}
