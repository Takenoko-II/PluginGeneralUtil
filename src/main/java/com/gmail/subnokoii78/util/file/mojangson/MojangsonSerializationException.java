package com.gmail.subnokoii78.util.file.mojangson;

import org.jetbrains.annotations.NotNull;

public class MojangsonSerializationException extends RuntimeException {
    protected MojangsonSerializationException(@NotNull String message) {
        super(message);
    }
}
