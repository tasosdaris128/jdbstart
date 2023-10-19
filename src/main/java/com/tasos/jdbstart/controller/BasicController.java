package com.tasos.jdbstart.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class BasicController implements HttpHandler {

    protected static final Logger logger = LogManager.getLogger(BasicController.class);

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
