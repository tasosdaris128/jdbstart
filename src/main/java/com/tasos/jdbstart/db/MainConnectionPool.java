package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/*
 *
 * Connection pool manager.
 *
 */
public class MainConnectionPool implements ConnectionPool {

    private static Logger logger = LogManager.getLogger(MainConnectionPool.class);

    private String url;
    private String user;
    private String password;
    
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();

    private static int POOL_SIZE = 30;
    private static int MAX_POOL_SIZE = 60;
    private static int MAX_TIMEOUT = 30;

    public static MainConnectionPool create(String url, String user, String password) throws SQLException {

        List<Connection> pool = new ArrayList<>();

        for (int i = 0; i < POOL_SIZE; i++) {
            pool.add(createConnection(url, user, password));
        }

        return new MainConnectionPool(url, user, password, pool);
    }

    private MainConnectionPool(String url, String user, String password, List<Connection> pool) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.connectionPool = pool;
    }

    private static Connection createConnection(String url, String user, String password) throws SQLException {
       return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        logger.info("Acquiring connection...");

        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection(this.url, this.user, this.password));
            } else {
                // @TODO: Handle properly
                throw new RuntimeException("Application has run out of available connections!");
            }
        }


        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        
        if (!connection.isValid(MAX_TIMEOUT)) {
            connection = createConnection(this.url, this.user, this.password);
        }

        connection.setAutoCommit(false);

        usedConnections.add(connection);

        logger.info("Used connections: {}", countUsed());

        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        logger.info("Releasing connection...");

        connectionPool.add(connection);

        return usedConnections.remove(connection);
    }

    @Override
    public void shutdown() {
        logger.info("Releasing connections...");
        
        try {
            this.usedConnections.forEach(this::releaseConnection);

            logger.info("Closing connections...");

            for (Connection connection: this.connectionPool) {
                connection.close();
            }

            connectionPool.clear();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

        
    @Override
    public int size() {
        return connectionPool.size() + usedConnections.size();
    }

    @Override
    public int countUsed() {
        return usedConnections.size();
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
