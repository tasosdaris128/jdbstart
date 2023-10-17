package com.tasos.jdbstart.utils;

public class NamedThreadLocal<T> extends ThreadLocal<T> {

    private final String name;

    public NamedThreadLocal(String name) {
        if (name.isEmpty()) throw new RuntimeException("Thread local name must not be empty!");
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
}
