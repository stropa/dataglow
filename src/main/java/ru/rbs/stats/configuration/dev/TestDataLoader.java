package ru.rbs.stats.configuration.dev;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;

public class TestDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(TestDataLoader.class);

    @Resource(name = "dataSource")
    private DataSource testDataSource;


    @PostConstruct
    public void loadData() throws SQLException {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("test_database.sql"));
        populator.populate(testDataSource.getConnection());

        final IDataTypeFactory dataTypeFactory = new HsqldbDataTypeFactory();
        IDatabaseTester loader = new DataSourceDatabaseTester(testDataSource) {
            @Override
            public IDatabaseConnection getConnection() throws Exception {
                final IDatabaseConnection result = super.getConnection();
                result.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
                result.getConfig().setFeature(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true);
                return result;
            }
        };
        try {
            ClassPathResource resource = new ClassPathResource("test_database/table-ordering.txt");
            String absolutePath = resource.getFile().getAbsolutePath();
            int indexLastSlash = absolutePath.lastIndexOf(File.separator);
            String dataSetPath = absolutePath.substring(0, indexLastSlash);
            CsvDataSet dataSet = new CsvDataSet(new File(dataSetPath));
            loader.setDataSet(dataSet);
            loader.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            //loader.setTearDownOperation(DatabaseOperation.DELETE_ALL);
            loader.onSetup();
            logger.info("Finished populating test data");
        } catch (Exception e) {
            logger.error("Failed to load test data", e);
        }

    }


}
