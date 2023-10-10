package com.tasos.jdbstart.model;

public class Stuff {
    private int id;
    private String placeholder;

    public Stuff() {}

    public Stuff(int id, String placeholder) {
        this.id = id;
        this.placeholder = placeholder;
    }

    public int getId() {
        return this.id;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String toString() {
        return "{\"id\": " + id + ", placeholder\": \"" + this.placeholder + "\"}";
    }
}
