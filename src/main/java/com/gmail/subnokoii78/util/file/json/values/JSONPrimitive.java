package com.gmail.subnokoii78.util.file.json.values;

import com.gmail.subnokoii78.util.file.json.JSONValue;

public abstract class JSONPrimitive<T> extends JSONValue<T> {
    protected JSONPrimitive(T value) {
        super(value);
    }

    public T getValue() {
        return value;
    }
}
