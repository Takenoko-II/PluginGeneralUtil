package com.gmail.subnokoii78.util.file.json;

public class JSONValue<T> {
    protected final T value;

    protected JSONValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return (value == null) ? "null" : value.toString();
    }
}
