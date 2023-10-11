package com.tasos.jdbstart;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.tasos.jdbstart.controller.BasicController;
import com.tasos.jdbstart.controller.InsertController;
import com.tasos.jdbstart.controller.v2.InsertControllerImproved;
import com.tasos.jdbstart.controller.SelectAllController;
import com.tasos.jdbstart.controller.v2.SelectAllControllerImproved;
import com.tasos.jdbstart.db.DataSourceGenerator;
import com.tasos.jdbstart.utils.Cache;
import com.tasos.jdbstart.utils.PropertyManager;

import com.zaxxer.hikari.HikariDataSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class App {

    public static void main(String args[]) {
        Logger logger = LogManager.getLogger(App.class);

        Properties properties;

        try {
            FileInputStream in = new FileInputStream("application.properties");
            properties = PropertyManager.loadProperties(in);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Unable to load application properties.");
        }

        Cache.getInstance().setProperties(properties);
        
        logger.info("Properties: {}", properties.toString());
        
        String url = properties.getProperty("url", "");
        String user = properties.getProperty("user", "");
        String password = properties.getProperty("password", "");

        // @Refactor: Catch potential exception.
        int port = Integer.parseInt(properties.getProperty("port"));

        logger.info("PG Host: {}", url);
        logger.info("PG user: {}", user);
        logger.info("API port: {}", port);

        try {
            HikariDataSource dataSource = DataSourceGenerator.generate(properties);
            
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.createContext("/ping", new BasicController());
            server.createContext("/insert", new InsertController(properties));
            server.createContext("/select", new SelectAllController(properties));
            server.createContext("/v2/insert", new InsertControllerImproved(dataSource));
            server.createContext("/v2/select", new SelectAllControllerImproved(dataSource));

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();

            logger.info("API listening at: {}", port);

            Runtime.getRuntime().addShutdownHook(new ShutdownHook(dataSource));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    /*
     * Making shure that the connection pool will close each connection after application
     * termination.
     */
    private static class ShutdownHook extends Thread {
        HikariDataSource dataSource;

        ShutdownHook(HikariDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void run() {
            if (this.dataSource != null) dataSource.close();
        }
    }
}
