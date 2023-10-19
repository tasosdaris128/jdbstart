package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConnectionHolder {

    private static final Logger logger = LogManager.getLogger(ConnectionHolder.class);

    private final static String SAVEPOINT_PREFIX = "SAVEPOINT_";

    private Connection currentConnection;

    private Savepoint currentSavepoint;

    private boolean isInTransaction = false;

    private int savepointCounter = 0;

    private int acquireCounter = 0;

    public ConnectionHolder(Connection connection) {
        this.currentConnection = connection;
    }

    public void setConnection(Connection connection) {
        if (this.currentConnection == null) {
            this.currentConnection = connection;
            return;
        }

        try {
            this.currentConnection.close();
            this.currentConnection = null;

            this.currentConnection = connection;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        return this.currentConnection;
    }

    public boolean hasConnection() {
        return (this.currentConnection != null);
    }

    public void setInTransaction(boolean inTransaction) {
        this.isInTransaction = inTransaction;
    }

    public boolean isInTransaction() {
        return this.isInTransaction;
    }

    public void reset() {
        this.isInTransaction = false;
    }

    public void increaseAcquireCounter() {
        this.acquireCounter++;
    }

    public void decreaseAcquireCounter() {
        this.acquireCounter--;

        if (this.acquireCounter < 0) this.acquireCounter = 0;
    }

    public void createSavepoint() {
        String savepointName = SAVEPOINT_PREFIX + this.savepointCounter;
        logger.info("Creating savepoint {}", savepointName);
        try {
            this.savepointCounter++;
            this.currentSavepoint = getConnection().setSavepoint(savepointName);
        } catch (SQLException e) {
            this.savepointCounter--;
            logger.error(e.getMessage(), e);
        }
    }

    public Savepoint getSavepoint() {
        return this.currentSavepoint;
    }

    public int getAcquireCounter() {
        return this.acquireCounter;
    }
    
}
