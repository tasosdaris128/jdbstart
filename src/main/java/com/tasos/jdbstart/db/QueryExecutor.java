package com.tasos.jdbstart.db;

import com.tasos.jdbstart.model.Response;

public interface QueryExecutor {

    public Response execute(String sql, MainConnectionPool connectionPool);

}
