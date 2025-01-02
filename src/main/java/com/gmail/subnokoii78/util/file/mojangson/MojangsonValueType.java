package com.gmail.subnokoii78.util.file.mojangson;

import org.jetbrains.annotations.NotNull;

public abstract class MojangsonValueType<T extends MojangsonValue<?>> {
    protected final Class<T> clazz;

    protected MojangsonValueType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract T cast(Object value);

    @Override
    public @NotNull String toString() {
        return clazz.getSimpleName();
    }
}
