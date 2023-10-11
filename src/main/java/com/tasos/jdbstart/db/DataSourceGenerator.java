package com.tasos.jdbstart.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Properties;

import javax.sql.DataSource;

public class DataSourceGenerator {
   
    public static HikariDataSource generate(Properties properties) {

        if (properties == null) {
            throw new RuntimeException("Unable to retrieve application properties object.");
        }
        
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(properties.getProperty("url", ""));
        config.setUsername(properties.getProperty("user", ""));
        config.setPassword(properties.getProperty("password", ""));

        // @TODO: Add these to application properties as well.
        config.setMaximumPoolSize(60);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048" );
        config.setAutoCommit(false);

        return new HikariDataSource(config);
    }

}
