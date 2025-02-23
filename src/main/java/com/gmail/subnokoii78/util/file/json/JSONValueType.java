package com.gmail.subnokoii78.util.file.json;

import org.jetbrains.annotations.NotNull;

public abstract class JSONValueType<T extends JSONValue<?>> {
    protected final Class<T> clazz;

    protected JSONValueType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract T cast(Object value);

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }
}
