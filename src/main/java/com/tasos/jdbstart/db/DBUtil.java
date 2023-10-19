package com.tasos.jdbstart.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class DBUtil {
    private static final Logger logger = LogManager.getLogger(DBUtil.class);

    @Deprecated
    public static void begin() {
        logger.info("Begin transaction...");

        try {

            ConnectionHolder holder = ConnectionManager.getConnectionHolder();

            holder.createSavepoint();

            ConnectionManager.updateConnectionHolder(holder);

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Deprecated
    public static void end() {
        logger.info("End transaction...");

        try {

            ConnectionHolder holder = ConnectionManager.getConnectionHolder();

            Connection connection = holder.getConnection();

            if (connection != null) {

                try {
                    logger.info("Committing transaction...");
                    connection.commit();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                    
                    try {

                        Savepoint savepoint = holder.getSavepoint();

                        if (savepoint != null) {
                            connection.rollback(savepoint);
                        } else {
                            connection.rollback();
                        }

                    } catch (SQLException se) {
                        logger.error(se.getMessage(), se);
                    }

                } finally {
                    connection.close();
                }

            }

            ConnectionManager.clear();

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static synchronized void doInTransaction(ThrowingConsumer consumer) {
        logger.info("Do transaction without result...");

        try {
            
            ConnectionHolder holder = ConnectionManager.getConnectionHolder();

            holder.increaseAcquireCounter();

            Connection connection = holder.getConnection();

            connection.setAutoCommit(false);

            holder.createSavepoint();

            ConnectionManager.updateConnectionHolder(holder);

            consumer.consume(connection);

            holder.decreaseAcquireCounter();

            if (holder.getAcquireCounter() == 0) {
                logger.info("Counter is zero. Committing...");

                connection.commit();

                connection.close();

                ConnectionManager.clear();
            } else {
                ConnectionManager.updateConnectionHolder(holder);
            }

        } catch (Exception e) {
            
            logger.error(e.getMessage(), e);

            try {

                ConnectionHolder holder = ConnectionManager.getConnectionHolder();

                holder.decreaseAcquireCounter();

                if (holder.getAcquireCounter() == 0) {
                    Connection connection = holder.getConnection();

                    Savepoint savepoint = holder.getSavepoint();

                    if (savepoint != null) {
                        connection.rollback(savepoint);
                    } else {
                        connection.rollback();
                    }

                    connection.close();

                    ConnectionManager.clear();
                } else {
                    ConnectionManager.updateConnectionHolder(holder);
                }

            } catch (SQLException se) {
                logger.error(se.getMessage(), se);
            }

        }
    }

    public static synchronized <T> T doInTransactionWithReturn(ThrowingFunction<T> function) {
        logger.info("Do transaction with result...");

        T element = null;

        try {

            ConnectionHolder holder = ConnectionManager.getConnectionHolder();

            holder.increaseAcquireCounter();

            Connection connection = holder.getConnection();

            connection.setAutoCommit(false);

            holder.createSavepoint();

            ConnectionManager.updateConnectionHolder(holder);

            element = function.executeAndReturn(connection);

            holder.decreaseAcquireCounter();

            if (holder.getAcquireCounter() == 0) {
                connection.commit();

                connection.close();

                ConnectionManager.clear();
            } else {
                ConnectionManager.updateConnectionHolder(holder);
            }

        } catch (Exception e) {
            
            logger.error(e.getMessage(), e);

            try {

                ConnectionHolder holder = ConnectionManager.getConnectionHolder();

                holder.decreaseAcquireCounter();

                if (holder.getAcquireCounter() == 0) {
                    Connection connection = holder.getConnection();

                    Savepoint savepoint = holder.getSavepoint();

                    if (savepoint != null) {
                        connection.rollback(savepoint);
                    } else {
                        connection.rollback();
                    }

                    connection.close();

                    ConnectionManager.clear();
                }

                ConnectionManager.updateConnectionHolder(holder);

            } catch (SQLException se) {
                logger.error(se.getMessage(), se);
            }

        }

        return element;
    } 
    
}
