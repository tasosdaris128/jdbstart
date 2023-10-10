package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.SQLException;

/*
 *
 * JDBC Connection pool interface.
 *
 */
public interface ConnectionPool {
    /*
     * Returns the available connection to the database.
     */
    Connection getConnection() throws SQLException;

    /*
     * Releases a given database.
     *
     * @param connection The given DB connection to close.
     */
    boolean releaseConnection(Connection connection);

    /*
     * Closes all the opened connections gracefully.
     */
    void shutdown();

    /*
     * Returns the connection string.
     */
    String getUrl();

    /*
     * Returns the DB user.
     */
    String getUser();

    /*
     * Returns the DB password.
     */
    String getPassword();

    /*
     * Returns the size of the connection pool.
     */
    int size();

    /*
     * Returns the count of the used connections.
     */
    int countUsed();
}
