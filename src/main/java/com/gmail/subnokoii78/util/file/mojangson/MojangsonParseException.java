package com.gmail.subnokoii78.util.file.mojangson;

import org.jetbrains.annotations.NotNull;

public class MojangsonParseException extends RuntimeException {
    protected MojangsonParseException(@NotNull String message) {
        super(message);
    }
}
