package com.tasos.jdbstart.db;

public interface QueryExecutor {

    public int execute(String sql, MainConnectionPool connectionPool);

}
