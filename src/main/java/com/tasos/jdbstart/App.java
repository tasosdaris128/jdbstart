package com.tasos.jdbstart;

import com.sun.net.httpserver.HttpServer;
import com.tasos.jdbstart.controller.BasicController;
import com.tasos.jdbstart.controller.InsertController;
import com.tasos.jdbstart.controller.SelectAllController;
import com.tasos.jdbstart.controller.v2.InsertControllerImproved;
import com.tasos.jdbstart.controller.v2.SelectAllControllerImproved;
import com.tasos.jdbstart.db.DataSourceGenerator;
import com.tasos.jdbstart.utils.ApplicationContext;
import com.tasos.jdbstart.utils.PropertyManager;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        Properties properties;

        try {
            FileInputStream in = new FileInputStream("application.properties");
            properties = PropertyManager.loadProperties(in);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Unable to load application properties.");
        }

        ApplicationContext.properties(properties);
        
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

            ApplicationContext.dataSource(dataSource);

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.createContext("/ping", new BasicController());
            server.createContext("/insert", new InsertController());
            server.createContext("/select", new SelectAllController());
            server.createContext("/v2/insert", new InsertControllerImproved(dataSource));
            server.createContext("/v2/select", new SelectAllControllerImproved(dataSource));

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();

            logger.info("API listening at: {}", port);

            Runtime.getRuntime().addShutdownHook(new ShutdownHook(dataSource, server));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    /*
     * Making sure that the connection pool will close each connection after application
     * termination.
     */
    private static class ShutdownHook extends Thread {
        HikariDataSource dataSource;
        HttpServer server;

        ShutdownHook(HikariDataSource dataSource, HttpServer server) {
            this.dataSource = dataSource;
            this.server = server;
        }

        @Override
        public void run() {
            logger.info("Gracefully shutting down the server. Goodbye!");
            if (this.dataSource != null) dataSource.close();
            if (this.server != null) server.stop(1);
        }
    }
}
