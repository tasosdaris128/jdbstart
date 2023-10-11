package com.tasos.jdbstart.controller;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.db.DBUtil;
import com.tasos.jdbstart.model.Response;
import com.tasos.jdbstart.model.Stuff;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import com.sun.net.httpserver.HttpExchange;

public class InsertController extends BasicController {

    private Properties properties;

    public InsertController() {}

    public InsertController(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());

        Stuff stuff = parse(httpExchange.getRequestBody(), Stuff.class);
        
        logger.info("Parsed request: {}", stuff.toString());

        String sql = "INSERT INTO stuff (placeholder) VALUES (?)";
        
        DBUtil.doInTranstaction(properties, (conn) -> {
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
