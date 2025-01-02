package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;

public final class MojangsonByte extends MojangsonNumber<Byte> {
    private MojangsonByte(byte value) {
        super(value);
    }

    @Override
    public Byte getValue() {
        return super.getValue();
    }

    public static @NotNull MojangsonByte valueOf(byte value) {
        return new MojangsonByte(value);
    }
}
