package com.gmail.subnokoii78.util.file.mojangson;

import org.jetbrains.annotations.NotNull;

public abstract class MojangsonValue<T> {
    protected final T value;

    protected MojangsonValue(T value) {
        this.value = value;
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }
}
