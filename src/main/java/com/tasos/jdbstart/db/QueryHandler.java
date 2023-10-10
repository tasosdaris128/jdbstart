package com.tasos.jdbstart.db;

import com.tasos.jdbstart.model.Response;

public class QueryHandler {

    public static Response query(String sql, MainConnectionPool pool, QueryExecutor executor) {
        return executor.execute(sql, pool);
    }

}
