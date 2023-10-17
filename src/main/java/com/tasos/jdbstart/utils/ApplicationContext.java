package com.tasos.jdbstart.utils;

import java.util.Properties;

import com.zaxxer.hikari.HikariDataSource;

public class ApplicationContext {

    private volatile Properties properties;

    private volatile HikariDataSource dataSource;

    private static ApplicationContext instance;

    private ApplicationContext() {}

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) instance = new ApplicationContext();

        return instance;
    }

    public static void properties(Properties properties) {
        getInstance().setProperties(properties);
    }

    public static Properties properties() {
        return getInstance().getProperties();
    }

    public static void dataSource(HikariDataSource dataSource) {
        getInstance().setDataSource(dataSource);
    }

    public static HikariDataSource dataSource() {
        return getInstance().getDataSource();
    }

    private void setProperties(Properties properties) {
        this.properties = properties;
    }

    private Properties getProperties() {
        return this.properties;
    }

    private void setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private HikariDataSource getDataSource() {
        return this.dataSource;
    }
    
}
