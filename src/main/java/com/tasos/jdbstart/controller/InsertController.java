package com.tasos.jdbstart.controller;

import com.tasos.jdbstart.db.DBUtil;
import com.tasos.jdbstart.model.Response;
import com.tasos.jdbstart.model.Stuff;

import java.io.IOException;
import java.sql.PreparedStatement;

import com.sun.net.httpserver.HttpExchange;

public class InsertController extends BasicController {

    public InsertController() {}

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());

        Stuff stuff = parse(httpExchange.getRequestBody(), Stuff.class);
        
        logger.info("Parsed request: {}", stuff.toString());

        final String outerSql = "INSERT INTO stuff (placeholder) VALUES (?)";
        final String innerSql = "INSERT INTO another_stuff (placeholder) VALUES (?)";

        DBUtil.doInTransaction((outerConn) -> {
            PreparedStatement outerStatement = outerConn.prepareStatement(outerSql);

            outerStatement.setString(1, stuff.getPlaceholder());

            int outerResult = outerStatement.executeUpdate();

            DBUtil.doInTransaction((innerConn) -> {
                PreparedStatement innerStatement = innerConn.prepareStatement(innerSql);

                innerStatement.setString(1, stuff.getPlaceholder());

                int innerResult = innerStatement.executeUpdate();

                DBUtil.doInTransaction((esotericConn) -> {
                    PreparedStatement esotericStatement = esotericConn.prepareStatement(innerSql);

                    esotericStatement.setString(1, stuff.getPlaceholder());

                    int esotericResult = esotericStatement.executeUpdate();

                    logger.info("Esoteric query result: {}", esotericResult);

                    if (esotericStatement != null) esotericStatement.close();
                });

                logger.info("Inner query result: {}", innerResult);

                if (innerStatement != null) innerStatement.close();
            });

            logger.info("Outer query result: {}", outerResult);

            if (outerStatement != null) outerStatement.close();
        });

        Response response = new Response(200, "{\"code\": 200, \"message\": \"OK\"}");
                
        respond(httpExchange, response.getCode(), response.getBody().toString());
    }

}
