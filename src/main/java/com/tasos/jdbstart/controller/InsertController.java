package com.tasos.jdbstart.controller;

import com.sun.net.httpserver.HttpHandler;
import com.tasos.jdbstart.db.MainConnectionPool;
import com.tasos.jdbstart.logger.Log;
import com.tasos.jdbstart.model.InsertRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
        Log.i("Received request from: %s", httpExchange.getRemoteAddress().toString());

        InsertRequest insertRequest = parse(httpExchange.getRequestBody(), InsertRequest.class);
        
        Log.i("Parsed request: %s", insertRequest.toString());
        
        int code = 200;
        
        String message = "{\"message\":\"OK\"}";
        
        respond(httpExchange, code, message);
    }

}
