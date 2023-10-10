package com.tasos.jdbstart;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpServer;

import com.tasos.jdbstart.db.MainConnectionPool;
import com.tasos.jdbstart.logger.Log;

public class App {
    public static void main(String args[]) {
        Log.init(true);
        
        String url = System.getenv("PGURL");
        String user = System.getenv("PGUSR");
        String password = System.getenv("PGPWD");

        // @Refactor: Catch potential exception.
        int port = Integer.parseInt(System.getenv("PORT"));

        Log.i("PG Host: %s", url);
        Log.i("PG user: %s", user);
        Log.i("API port: %d", port);

        try {
            MainConnectionPool connectionPool = MainConnectionPool.create(url, user, password);
            
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.setExecutor(null);
            server.start();

            Log.i("API listening at: %d", port);

            Runtime.getRuntime().addShutdownHook(new ShutdownHook(connectionPool));
        } catch (SQLException | IOException e) {
            Log.exc(e);
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
