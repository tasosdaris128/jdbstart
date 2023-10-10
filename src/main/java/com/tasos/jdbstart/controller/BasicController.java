package com.tasos.jdbstart.controller;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.logger.Log;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

public class BasicController implements HttpHandler {
    
    public BasicController() {}

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Log.i("Received request from: %s", httpExchange.getRemoteAddress().toString());

        int code = 200;
        
        String message = "{\"message\":\"OK\"}";
        
        httpExchange.sendResponseHeaders(code, message.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(message.getBytes());
        outputStream.close();
    }

}
