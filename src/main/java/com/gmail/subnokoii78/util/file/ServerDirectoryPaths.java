package com.gmail.subnokoii78.util.file;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public enum ServerDirectoryPaths {
    LATEST_LOG("logs/latest.log");

    private final Path path;

    private final FileExtension<?> extension;

    ServerDirectoryPaths(@NotNull String path) {
        this.path = Path.of(path);
        this.extension = FileExtension.get(path);
    }

    public Path getPath() {
        return path;
    }

    public FileExtension<?> getExtension() {
        return extension;
    }
}
