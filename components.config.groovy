import com.jolbox.bonecp.BoneCPDataSource

// TODO: move datasources to DB
beans {
    dataSource(BoneCPDataSource) {
        driverClass = "..."
        jdbcUrl = "..."
        //maxPoolSize = 20
        user = "XXX"
        password = "XXX"
    }

}