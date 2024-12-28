package com.gmail.subnokoii78.util.file.json;

import org.jetbrains.annotations.NotNull;

public class JSONSerializeException extends RuntimeException {
    public JSONSerializeException() {
        super();
    }

    public JSONSerializeException(@NotNull String message) {
        super(message);
    }

    public JSONSerializeException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
