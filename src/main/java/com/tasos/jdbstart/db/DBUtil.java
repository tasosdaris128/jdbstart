package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.tasos.jdbstart.utils.Cache;

public class DBUtil {
    
    public static void doInTranstaction(ThrowingConsumer consumer) {
        Logger logger = LogManager.getLogger(DBUtil.class);

        Properties properties = Cache.getInstance().getProperties();

        if (properties == null) {
            throw new RuntimeException("Unable to get access to the application properties.");
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(
                properties.getProperty("url", ""),
                properties.getProperty("user", ""),
                properties.getProperty("password", "")
            );

            connection.setAutoCommit(false);

            consumer.consume(connection);

            connection.commit();
        } catch (Exception e) {
            
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException se) {
                logger.error(se.getMessage(), se); 
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

    public static <T> T doInTranstactionWithReturn(ThrowingFunction<T> function) {
        Logger logger = LogManager.getLogger(DBUtil.class);

        Properties properties = Cache.getInstance().getProperties();

        if (properties == null) {
            throw new RuntimeException("Unable to get access to the application properties.");
        }

        T element;

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(
                properties.getProperty("url", ""),
                properties.getProperty("user", ""),
                properties.getProperty("password", "")
            );

            connection.setAutoCommit(false);

            element = function.executeAndReturn(connection);

            connection.commit();
        } catch (Exception e) {
            
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException se) {
                logger.error(se.getMessage(), se); 
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
