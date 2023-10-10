package com.tasos.jdbstart.model;

public class InsertRequest {
    private String placeholder;

    public InsertRequest() {}

    public InsertRequest(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String toString() {
        return "{\"placeholder\": \"" + this.placeholder + "\"}";
    }
}
