import com.jolbox.bonecp.BoneCPDataSource

beans {
    dataSource(BoneCPDataSource) {
        driverClass = "oracle.jdbc.driver.OracleDriver"
        jdbcUrl = "jdbc:oracle:thin:@localhost:1621:rbs"
        //maxPoolSize = 20
        user = "XXX"
        password = "XXX"
    }

}