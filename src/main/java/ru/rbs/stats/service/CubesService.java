package ru.rbs.stats.service;

import com.google.gson.Gson;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;
import org.jooq.DSLContext;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.rbs.stats.configuration.SystemProperties;
import ru.rbs.stats.model.Sequences;
import ru.rbs.stats.model.tables.daos.CubeDescriptionDao;
import ru.rbs.stats.store.CubeDescription;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CubesService {

    private static final Logger logger = LoggerFactory.getLogger(CubesService.class);

    @Resource
    private CubeDescriptionDao cubeDescriptionDao;

    @Resource
    private InfluxDB influxDB;

    @Resource
    private SystemProperties systemProperties;

    @Resource
    private DSLContext dslContext;

    @Resource
    private ModelMapper modelMapper;

    public Collection<CubeDescription> getAllCubes() {
        List<ru.rbs.stats.model.tables.pojos.CubeDescription> all = cubeDescriptionDao.findAll();
        return all.stream().map(this::fromPojo).collect(toList());
    }

    private CubeDescription fromPojo(ru.rbs.stats.model.tables.pojos.CubeDescription m) {
        CubeDescription cd = new CubeDescription(m.getName());
        cd.setId(m.getId());
        cd.setAggregates(
                Arrays.asList(m.getAggregates().substring(1, m.getAggregates().length() - 1)  // remove '[' and ']'
                        .split(",")).stream().map(s -> s = s.trim()).collect(toList()));
        cd.setDimensions(
                Arrays.asList(m.getDimensions().substring(1, m.getDimensions().length() - 1)
                        .split(",")).stream().map(s -> s = s.trim()).collect(toList()));

        Map<String, String> map = new Gson().<Map<String, String>>fromJson(m.getTypes(), Map.class);
        for (String key : map.keySet()) {
            cd.getTypes().put(key, CubeDescription.CubeDataType.valueOf(map.get(key)));
        }
        return cd;
    }


    public CubeDescription get(Long id) {
        Optional<ru.rbs.stats.model.tables.pojos.CubeDescription> m = Optional.of(cubeDescriptionDao.fetchOneById(id));
        return m.map(this::fromPojo).orElse(null);
    }

    public ru.rbs.stats.model.tables.pojos.CubeDescription saveCubeDescription(CubeDescription cubeDescription) {
        ru.rbs.stats.model.tables.pojos.CubeDescription cd = new ru.rbs.stats.model.tables.pojos.CubeDescription();
        cd.setName(cubeDescription.getName());
        cd.setId(dslContext.nextval(Sequences.CUBE_DESCRIPTION_IDS).longValue());
        cd.setAggregates(cubeDescription.getAggregates().toString());
        cd.setDimensions(cubeDescription.getDimensions().toString());
        cd.setTypes(new Gson().toJson(cubeDescription.getTypes()));
        cubeDescriptionDao.insert(cd);
        return cd;
    }

    public Map<String, List<String>> getAxes(Long cubeId) {
        // for now just making select distinct request to influxdb to get all dimensions for cube
        ru.rbs.stats.model.tables.pojos.CubeDescription cd = cubeDescriptionDao.fetchOneById(cubeId);
        if (cd == null) return null;
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String dimension : fromPojo(cd).getDimensions()) {
            List<Serie> list = null;
            try {
                list = influxDB.query(systemProperties.getProperty(SystemProperties.INFLUXDB_DATABASE_NAME), "select distinct(" + dimension + ") from " + cd.getName(), TimeUnit.SECONDS);
                List<String> distinct = list.get(0).getRows().stream().map(m -> (String) m.get("distinct")).collect(Collectors.toList());
                result.put(dimension, distinct);
            } catch (Exception e) {
                logger.debug("Failed to get distinct dimensions values for cube " + cd.getName(), e );
            }
        }
        return result;
    }
}
