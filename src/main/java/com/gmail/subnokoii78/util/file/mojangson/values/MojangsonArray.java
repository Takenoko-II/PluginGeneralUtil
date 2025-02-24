package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;

public abstract class MojangsonArray<T> extends MojangsonPrimitive<T> implements MojangsonStructure {
    protected MojangsonArray(@NotNull T value) {
        super(value);

        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException("配列型でない値はMojangsonArrayに変換できません");
        }
    }

    public abstract @NotNull T toPrimitiveArray();

    public abstract @NotNull MojangsonList toList();
}
