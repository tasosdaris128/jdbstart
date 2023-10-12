package com.tasos.jdbstart.controller;

import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BasicController implements HttpHandler {

    protected static Logger logger = LogManager.getLogger(BasicController.class);

    public BasicController() {}

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Received request from: {}", httpExchange.getRemoteAddress().toString());

        int code = 200;
        
        String message = "{\"message\":\"OK\"}";
        
        respond(httpExchange, code, message);
    }

    public void respond(HttpExchange httpExchange, int code, String message) throws IOException {
        httpExchange.sendResponseHeaders(code, message.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(message.getBytes());
        outputStream.close();
    }

    public <T> T parse(InputStream in, Class<T> valueType) throws IOException, JsonMappingException {
        String requestBody = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        
        logger.info("Request body: {}", requestBody);

        ObjectMapper mapper = new ObjectMapper();

        T o = mapper.readValue(requestBody, valueType);
        
        logger.info("parse() -> {}", o.toString());

        return o;
    }

}
