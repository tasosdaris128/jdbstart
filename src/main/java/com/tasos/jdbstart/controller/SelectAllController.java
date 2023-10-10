package com.tasos.jdbstart.controller;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.db.MainConnectionPool;
import com.tasos.jdbstart.db.QueryHandler;
import com.tasos.jdbstart.logger.Log;
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
        Log.i("Received request from: %s", httpExchange.getRemoteAddress().toString());
        
        int code = 200;
       
        String sql = "SELECT id, placeholder FROM stuff";

        code = QueryHandler.query(sql, pool, (s, p) -> {
            Log.i("Executing: %s", s);
            
            int c = 200;

            Connection connection = null;
            PreparedStatement statement = null;

            try {
                connection = p.getConnection();

                statement = connection.prepareStatement(s);

                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    Stuff stuff = new Stuff(result.getInt(1), result.getString(2));
                    Log.d("Stuff: %s", stuff.toString());
                }
            } catch (SQLException e) {
                c = 500;
                Log.exc(e);
            } finally {

                try {
                    if (statement != null) statement.close();
                } catch (SQLException se) {
                    c = 500;
                    Log.exc(se);
                }
                
                if (connection != null) p.releaseConnection(connection);
            }

            return c;
        });
        
        String message = "{\"message\":\"OK\"}";
        
        respond(httpExchange, code, message);
    }

}
