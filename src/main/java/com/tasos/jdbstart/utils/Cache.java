package com.tasos.jdbstart.utils;

import java.util.Properties;

public class Cache {

    private volatile Properties properties;

    private static Cache instance;

    private Cache() {}

    public static synchronized Cache getInstance() {
        if (instance == null) instance = new Cache();

        return instance;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }
    
}
