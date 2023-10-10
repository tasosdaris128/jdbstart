package com.tasos.jdbstart;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.tasos.jdbstart.controller.BasicController;
import com.tasos.jdbstart.controller.InsertController;
import com.tasos.jdbstart.controller.SelectAllController;
import com.tasos.jdbstart.db.MainConnectionPool;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class App {
    public static void main(String args[]) {
        Logger logger = LogManager.getLogger(App.class);
        
        String url = System.getenv("PGURL");
        String user = System.getenv("PGUSR");
        String password = System.getenv("PGPWD");

        // @Refactor: Catch potential exception.
        int port = Integer.parseInt(System.getenv("PORT"));

        logger.info("PG Host: {}", url);
        logger.info("PG user: {}", user);
        logger.info("API port: {}", port);

        try {
            MainConnectionPool connectionPool = MainConnectionPool.create(url, user, password);
            
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.createContext("/ping", new BasicController());
            server.createContext("/insert", new InsertController(connectionPool));
            server.createContext("/select", new SelectAllController(connectionPool));

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();

            logger.info("API listening at: {}", port);

            Runtime.getRuntime().addShutdownHook(new ShutdownHook(connectionPool));
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    /*
     * Making shure that the connection pool will close each connection after application
     * termination.
     */
    private static class ShutdownHook extends Thread {
        MainConnectionPool pool;

        ShutdownHook(MainConnectionPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            if (this.pool != null) pool.shutdown();
        }
    }
}
