package com.gmail.subnokoii78.util.file.json;

import org.jetbrains.annotations.NotNull;

public class JSONParseException extends RuntimeException {
    protected JSONParseException(@NotNull String message, @NotNull String json, int location) {
        super(JSONParseException.createMessage(message, json, location));
    }

    protected JSONParseException(@NotNull String message, @NotNull String json, int location, @NotNull Throwable cause) {
        super(JSONParseException.createMessage(message,json, location), cause);
    }

    protected static @NotNull String createMessage(@NotNull String message, @NotNull String json, int location) {
        return new StringBuilder()
            .append(message)
            .append(": ")
            .append(json, Math.max(0, location - 8), Math.max(0, location - 1))
            .append(" >> ")
            .append(json.charAt(location))
            .append(" << ")
            .append(json, Math.min(location + 1, json.length()), Math.min(location + 8, json.length()))
            .toString();
    }
}
