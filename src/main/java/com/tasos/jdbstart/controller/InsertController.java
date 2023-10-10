package com.tasos.jdbstart.controller;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.db.MainConnectionPool;
import com.tasos.jdbstart.db.QueryHandler;
import com.tasos.jdbstart.model.Response;
import com.tasos.jdbstart.model.Stuff;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

public class InsertController extends BasicController {
    
    private MainConnectionPool pool;

    public InsertController() {}

    public InsertController(MainConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());

        Stuff stuff = parse(httpExchange.getRequestBody(), Stuff.class);
        
        logger.info("Parsed request: {}", stuff.toString());

        String sql = "INSERT INTO stuff (placeholder) VALUES (?)";

        Response response = QueryHandler.query(sql, pool, (s, p) -> {
            logger.info("Executing: {}", s);
            
            int c = 200;

            Connection connection = null;
            PreparedStatement statement = null;

            try {
                connection = p.getConnection();
                
                // Let's commit manually.
                connection.setAutoCommit(false);

                statement = connection.prepareStatement(s);

                statement.setString(1, stuff.getPlaceholder());

                int result = statement.executeUpdate();
                
                connection.commit();

                logger.info("Query result: {}", result);
            } catch (SQLException e) {
                
                try {
                    if (connection != null) connection.rollback();
                } catch (SQLException se) {
                    logger.error(se.getMessage(), se);
                }
                
                c = 500;
                logger.error(e.getMessage(), e);
            } finally {

                try {
                    if (statement != null) statement.close();
                } catch (SQLException se) {
                    c = 500;
                    logger.error(se.getMessage(), se);
                }
                
                if (connection != null) p.releaseConnection(connection);
            }

            return new Response(c, "OK");
        });
        
        respond(httpExchange, response.getCode(), response.getBody().toString());
    }

}
