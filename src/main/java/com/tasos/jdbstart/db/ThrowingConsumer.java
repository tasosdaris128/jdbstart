package com.tasos.jdbstart.db;

import java.sql.Connection;

@FunctionalInterface
public interface ThrowingConsumer {
    void consume(Connection connection) throws Exception;
}

