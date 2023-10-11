package com.tasos.jdbstart.db;

import java.sql.Connection;

@FunctionalInterface
public interface ThrowingFunction<T> {

    T executeAndReturn(Connection connection) throws Exception;
    
}
