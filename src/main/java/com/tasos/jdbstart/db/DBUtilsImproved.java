package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.tasos.jdbstart.utils.Cache;

public class DBUtilsImproved {
    
    public static void doInTranstaction(MainConnectionPool pool, ThrowingConsumer consumer) {
        Logger logger = LogManager.getLogger(DBUtil.class);

        Connection connection = null;

        try {
            connection = pool.getConnection();

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
            if (connection != null) pool.releaseConnection(connection);
        }
    }

    public static <T> T doInTranstactionWithReturn(MainConnectionPool pool, ThrowingFunction<T> function) {
        Logger logger = LogManager.getLogger(DBUtil.class);

        T element;

        Connection connection = null;

        try {
            connection = pool.getConnection();

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
            if (connection != null) pool.releaseConnection(connection);
        }

        return element;
    } 
    
}
