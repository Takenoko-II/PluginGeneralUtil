package com.gmail.subnokoii78.util.file;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public abstract class FileExtension<T> {
    private FileExtension() {}

    abstract @NotNull List<T> read(@NotNull Path path);

    abstract void write(@NotNull Path path, @NotNull List<T> value);

    public static FileExtension<?> get(@NotNull String path) {
        if (path.endsWith(".txt")) return TEXT;
        else if (path.endsWith(".log")) return TEXT;
        else if (path.endsWith(".properties")) return TEXT;
        else if (path.endsWith(".yml")) return TEXT;
        else if (path.endsWith(".json")) return TEXT;
        else if (path.endsWith(".bin")) return BINARY;
        else if (path.endsWith(".dat")) return BINARY;
        else if (path.endsWith(".nbt")) return BINARY;
        else throw new RuntimeException("登録されていない拡張子です: " + path);
    }

    public static final FileExtension<String> TEXT = new FileExtension<String>() {
        @Override
        @NotNull List<String> read(@NotNull Path path) {
            try {
                return Files.readAllLines(path, StandardCharsets.UTF_8);
            }
            catch (IOException e) {
                throw new RuntimeException("ファイルの読み取りに失敗しました", e);
            }
        }

        @Override
        void write(@NotNull Path path, @NotNull List<String> value) {
            try {
                Files.write(path, value, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            catch (IOException e) {
                throw new RuntimeException("ファイルの書き込みに失敗しました", e);
            }
        }
    };

    public static final FileExtension<Byte> BINARY = new FileExtension<Byte>() {
        @Override
        @NotNull List<Byte> read(@NotNull Path path) {
            try {
                final byte[] bytes = Files.readAllBytes(path);
                final List<Byte> list = new ArrayList<>();
                for (final byte value : bytes) {
                    list.add(value);
                }
                return List.copyOf(list);
            }
            catch (IOException e) {
                throw new RuntimeException("ファイルの読み取りに失敗しました", e);
            }
        }

        @Override
        void write(@NotNull Path path, @NotNull List<Byte> value) {
            try {
                final byte[] clone = new byte[value.size()];

                for (int i = 0; i < value.size(); i++) {
                    clone[i] = value.get(i);
                }

                Files.write(path, clone);
            }
            catch (IOException e) {
                throw new RuntimeException("ファイルの書き込みに失敗しました", e);
            }
        }
    };
}
