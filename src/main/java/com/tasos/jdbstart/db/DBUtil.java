package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.tasos.jdbstart.utils.Cache;

public class DBUtil {
    private static Logger logger = LogManager.getLogger(DBUtil.class);

    public static void doInTranstaction(Properties properties, ThrowingConsumer consumer) {
        
        Connection connection = null;
        Savepoint beforeConsumption = null;

        try {
            connection = DriverManager.getConnection(
                properties.getProperty("url", ""),
                properties.getProperty("user", ""),
                properties.getProperty("password", "")
            );

            connection.setAutoCommit(false);

            beforeConsumption = connection.setSavepoint("beforeConsumption");

            consumer.consume(connection);

            connection.commit();
        } catch (Exception e) {
            
            rollbackToSavePoint: {
                try {

                    if (connection == null) break rollbackToSavePoint;

                    if (beforeConsumption != null) {
                        connection.rollback(beforeConsumption);
                        break rollbackToSavePoint;
                    }

                    connection.rollback();

                } catch (SQLException se) {
                    logger.error(se.getMessage(), se); 
                }
            }

            logger.error(e.getMessage(), e);

            throw new RuntimeException("Unable to commit transaction.");

        } finally {
            
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    public static <T> T doInTranstactionWithReturn(Properties properties, ThrowingFunction<T> function) {
        
        T element;

        Connection connection = null;
        Savepoint beforeFunction = null;

        try {
            connection = DriverManager.getConnection(
                properties.getProperty("url", ""),
                properties.getProperty("user", ""),
                properties.getProperty("password", "")
            );

            connection.setAutoCommit(false);

            beforeFunction = connection.setSavepoint("beforeFunction");

            element = function.executeAndReturn(connection);

            connection.commit();
        } catch (Exception e) {
            
            rollbackToSavePoint: {
                try {

                    if (connection == null) break rollbackToSavePoint;

                    if (beforeFunction != null) {
                        connection.rollback(beforeFunction);
                        break rollbackToSavePoint;
                    }

                    connection.rollback();

                } catch (SQLException se) {
                    logger.error(se.getMessage(), se); 
                }
            }

            logger.error(e.getMessage(), e);

            throw new RuntimeException("Unable to commit transaction.");

        } finally {
            
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }

        }

        return element;
    } 
    
}
