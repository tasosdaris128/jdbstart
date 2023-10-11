package com.tasos.jdbstart.controller;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.db.DBUtil;
import com.tasos.jdbstart.db.MainConnectionPool;
import com.tasos.jdbstart.model.Response;
import com.tasos.jdbstart.model.Stuff;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.sun.net.httpserver.HttpExchange;

public class SelectAllController extends BasicController {

    private Properties properties;

    public SelectAllController() {}

    public SelectAllController(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());
        
        Response response = new Response(200, "");

        String sql = "SELECT id, placeholder FROM stuff";

        List<Stuff> stuffs = DBUtil.doInTranstactionWithReturn(properties, (conn) -> {
            List<Stuff> s = new ArrayList<>();

            PreparedStatement statement = conn.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Stuff stuff = new Stuff(result.getInt(1), result.getString(2));
                logger.info("Stuff: {}", stuff.toString());
                boolean addedToStuff = s.add(stuff);
                logger.info("Added to stuffs? {}", addedToStuff);
            }

            return s;
        });

        prepareResponse: {
            if (stuffs == null) {
                response.setCode(500);
                break prepareResponse;
            }

            response.setBody(stuffs);
        }
               
        respond(httpExchange, response.getCode(), response.getBody().toString());
    }

}
