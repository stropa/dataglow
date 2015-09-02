import com.mchange.v2.c3p0.ComboPooledDataSource

beans {
    dataSource(ComboPooledDataSource) {
        driverClass = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/rbsbase"
        maxPoolSize = 20
        user = "rbs"
        password = "rbs"
    }

}