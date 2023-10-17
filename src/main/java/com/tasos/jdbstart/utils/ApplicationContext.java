package com.tasos.jdbstart.utils;

import java.util.Properties;

public class ApplicationContext {

    private volatile Properties properties;

    private static ApplicationContext instance;

    private ApplicationContext() {}

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) instance = new ApplicationContext();

        return instance;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }
    
}
