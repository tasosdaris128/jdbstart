package com.tasos.jdbstart;

import java.sql.Connection;
import java.sql.SQLException;

import com.tasos.jdbstart.db.MainConnectionPool;
import com.tasos.jdbstart.logger.Log;

public class App {
    public static void main(String args[]) {
        Log.init(true);
        
        // @Cleanup: Remove flex call.
        // Log.flex();
        
        MainConnectionPool connectionPool = null;

        try {
            // @Todo: Retrieve from environment variable.
            connectionPool = MainConnectionPool.create("jdbc:postgresql://localhost/jdbstart", "postgres", "postgres");

            Connection aConnection = connectionPool.getConnection();
            Connection bConnection = connectionPool.getConnection();

            Log.i("Initial size of connection pool: %d", connectionPool.size());
            Log.i("Initial count of used connections: %d", connectionPool.countUsed());

            boolean releaseResultA = connectionPool.releaseConnection(aConnection);
            
            Log.i("Result of releasing connection A: %b", releaseResultA);
            Log.i("Size of connection pool after releasing: %d", connectionPool.size());
            Log.i("Count of used connections after release: %d", connectionPool.countUsed());

            boolean releaseResultB = connectionPool.releaseConnection(bConnection);

            Log.i("Result of releasing connection B: %b", releaseResultB);
            Log.i("Size of connection pool after releasing: %d", connectionPool.size());
            Log.i("Count of used connections after release: %d", connectionPool.countUsed());

        } catch (SQLException e) {
            Log.exc(e);
        } finally {
            if (connectionPool != null) connectionPool.shutdown();
        }
    }
}
