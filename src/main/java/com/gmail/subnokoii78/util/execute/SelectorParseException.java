package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;

public class SelectorParseException extends RuntimeException {
    protected SelectorParseException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    protected SelectorParseException(@NotNull String message) {
        super(message);
    }
}
