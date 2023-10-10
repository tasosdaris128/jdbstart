package com.tasos.jdbstart.model;

public class Response {

    private int code;
    private Object body;

    public Response() {}

    public Response(int code, Object body) {
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return this.code;
    }

    public Object getBody() {
        return this.body;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "{\"code\": " + code + ", \"body\": " + body.toString() + "}";
    }
}
