package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;

public enum ScanMode {
    ALL("all"),

    MASKED("masked");

    private final String id;

    ScanMode(@NotNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
