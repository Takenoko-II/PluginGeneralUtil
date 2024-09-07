package com.gmail.subnokoii78.util.file;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ServerDirectoryFileManager {
    protected final Path path;

    public ServerDirectoryFileManager(@NotNull ServerDirectoryPaths path) {
        this.path = path.getPath();
    }

    private ServerDirectoryFileManager(@NotNull Path path) {
        this.path = path;
    }

    public boolean exist() {
        return Files.exists(path);
    }

    public void create() {
        if (exist()) return;

        if (path.getParent() == null) {
            new ServerDirectoryFileManager(path.subpath(0, path.getNameCount() - 1)).create();
        }

        try {
            Files.createFile(path);
        }
        catch (IOException e) {
            throw new RuntimeException("ファイルを作成できませんでした", e);
        }
    }

    public void delete() {
        try {
            Files.delete(path);
        }
        catch (IOException e) {
            throw new RuntimeException("ファイルを削除できませんでした", e);
        }
    }

    public <T> @NotNull List<T> read(@NotNull FileExtension<T> extension) {
        return extension.read(path);
    }

    public <T> void overwrite(@NotNull FileExtension<T> extension, @NotNull List<T> values) {
        extension.write(path, values);
    }

    public <T> void writeAdd(@NotNull FileExtension<T> extension, @NotNull T value) {
        final List<T> list = new ArrayList<>(read(extension));
        list.add(value);
        overwrite(extension, list);
    }

    public void writeAdd(@NotNull FileExtension<String> extension, @NotNull String value, int line) {
        final List<String> list = new ArrayList<>(read(extension));
        list.add(line, value);
        overwrite(extension, list);
    }

    public void erase(@NotNull FileExtension<String> extension, int line) {
        final List<String> list = new ArrayList<>(read(extension));
        list.remove(line);
        overwrite(extension, list);
    }
}
