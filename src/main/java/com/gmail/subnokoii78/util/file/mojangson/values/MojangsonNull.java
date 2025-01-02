package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import org.jetbrains.annotations.NotNull;

public final class MojangsonNull extends MojangsonValue<Object> {
    private MojangsonNull() {
        super(null);
    }

    public Object getNull() {
        return value;
    }

    @Override
    public @NotNull String toString() {
        return "null";
    }

    public static final MojangsonNull NULL = new MojangsonNull();
}
