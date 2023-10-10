package com.tasos.jdbstart.db;

public class QueryHandler {

    public static int query(String sql, MainConnectionPool pool, QueryExecutor executor) {
        return executor.execute(sql, pool);
    }

}
