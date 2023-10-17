package com.tasos.jdbstart.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.tasos.jdbstart.utils.ApplicationContext;
import com.tasos.jdbstart.utils.NamedThreadLocal;

public abstract class ConnectionManager {

    public static final ThreadLocal<ConnectionHolder> connectionInTransaction = new NamedThreadLocal<>("Connection in transaction");

    public static ConnectionHolder getConnectionHolder() throws SQLException {
        ConnectionHolder holder = connectionInTransaction.get();

        if (holder == null) {
            DataSource dataSource = ApplicationContext.dataSource();

            if (dataSource == null) throw new RuntimeException("Application context data source is null!");

            ConnectionHolder newHolder = new ConnectionHolder(dataSource.getConnection());

            connectionInTransaction.set(newHolder);

            return newHolder;
        }

        return holder;
    }

    public static void upateConnectionHolder(ConnectionHolder holder) {
        connectionInTransaction.remove();
        connectionInTransaction.set(holder);
    }

    public static void clear() {
        connectionInTransaction.remove();
    }
    
}
