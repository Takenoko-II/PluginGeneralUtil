package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;

public abstract class MojangsonPrimitive<T> extends MojangsonValue<T> {
    protected MojangsonPrimitive(T value) {
        super(value);
    }

    public T getValue() {
        return value;
    }
}
