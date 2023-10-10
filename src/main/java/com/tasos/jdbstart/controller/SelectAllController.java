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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

public class SelectAllController extends BasicController {
    
    private MainConnectionPool pool;

    public SelectAllController() {}

    public SelectAllController(MainConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());

        String sql = "SELECT id, placeholder FROM stuff";

        Response response = QueryHandler.query(sql, pool, (s, p) -> {
            logger.info("Executing: {}", s);
            
            int c = 200;

            Connection connection = null;
            PreparedStatement statement = null;

            List<Stuff> stuffs = new ArrayList<>();

            try {
                connection = p.getConnection();

                statement = connection.prepareStatement(s);

                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    Stuff stuff = new Stuff(result.getInt(1), result.getString(2));
                    logger.info("Stuff: {}", stuff.toString());
                    boolean addedToStuffs = stuffs.add(stuff);
                    logger.info("Added to stuffs? {}", addedToStuffs);
                }
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

            return new Response(c, stuffs);
        });
        
        respond(httpExchange, response.getCode(), response.getBody().toString());
    }

}
