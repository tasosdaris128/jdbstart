package com.tasos.jdbstart.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

    public static Properties loadProperties(InputStream in) throws IOException {
        Properties properties = new Properties();

        if (in == null) {
            throw new RuntimeException("InputStream is null");
        }

        properties.load(in);

        in.close();

        return properties;
    }
    
}
