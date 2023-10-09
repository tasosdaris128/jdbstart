package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 *
 * Connection pool manager.
 *
 */
public class MainConnectionPool implements ConnectionPool {
    
    private String url;
    private String user;
    private String password;
    
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();

    private static int POOL_SIZE = 30;
    private static int MAX_POOL_SIZE = 60;
    private static int MAX_TIMEOUT = 30;

    public static MainConnectionPool create(String url, String user, String password) throws SQLException {

        List<Connection> pool = new ArrayList<>(POOL_SIZE);

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

        usedConnections.add(connection);

        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);

        return usedConnections.remove(connection);
    }

    @Override
    public void shutdown() throws SQLException {
        this.usedConnections.forEach(this::releaseConnection);

        for (Connection connection: this.connectionPool) {
            connection.close();
        }

        connectionPool.clear();
    }

        
    @Override
    public int size() {
        return connectionPool.size() + usedConnections.size();
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
