package com.tasos.jdbstart.controller.v2;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.controller.BasicController;
import com.tasos.jdbstart.db.DBUtilsImproved;
import com.tasos.jdbstart.db.MainConnectionPool;
import com.tasos.jdbstart.model.Response;
import com.tasos.jdbstart.model.Stuff;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.sun.net.httpserver.HttpExchange;

public class InsertControllerImproved extends BasicController {

    private MainConnectionPool pool;

    public InsertControllerImproved() {}

    public InsertControllerImproved(MainConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());

        Stuff stuff = parse(httpExchange.getRequestBody(), Stuff.class);
        
        logger.info("Parsed request: {}", stuff.toString());

        String sql = "INSERT INTO stuff (placeholder) VALUES (?)";
        
        DBUtilsImproved.doInTranstaction(pool, (conn) -> {
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, stuff.getPlaceholder());

            int result = statement.executeUpdate();

            logger.info("Query result: {}", result);

            if (statement != null) statement.close();
        });

        Response response = new Response(200, "{\"code\": 200, \"message\": \"OK\"}");
                
        respond(httpExchange, response.getCode(), response.getBody().toString());
    }

}
