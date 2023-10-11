package com.tasos.jdbstart.controller.v2;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.controller.BasicController;
import com.tasos.jdbstart.db.DBUtilsImproved;
import com.tasos.jdbstart.model.Response;
import com.tasos.jdbstart.model.Stuff;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import com.sun.net.httpserver.HttpExchange;

public class InsertControllerImproved extends BasicController {

    private DataSource dataSource;

    public InsertControllerImproved() {}

    public InsertControllerImproved(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());

        Stuff stuff = parse(httpExchange.getRequestBody(), Stuff.class);
        
        logger.info("Parsed request: {}", stuff.toString());

        String sql = "INSERT INTO stuff (placeholder) VALUES (?)";
        
        DBUtilsImproved.doInTranstaction(dataSource, (conn) -> {
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
