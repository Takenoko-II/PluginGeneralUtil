package com.gmail.subnokoii78.util.file.json;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Function;

public class JSONFile {
    private final Path path;

    public JSONFile(@NotNull Path path) {
        this.path = path;

        if (!path.toFile().isFile()) {
            throw new IllegalArgumentException("そのパスはファイルパスとして無効です");
        }
    }

    public JSONFile(@NotNull String path) {
        this(Path.of(path));
    }

    public JSONFile(@NotNull File file) {
        this(file.toPath());
    }

    protected @NotNull String readAsString() throws IllegalStateException {
        try {
            return String.join("\n", Files.readAllLines(path));
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの読み取りに失敗しました", e);
        }
    }

    protected void writeDirectly(@NotNull String json) throws IllegalStateException {
        try {
            Files.write(
                path,
                Arrays.asList(json.split("\\n")),
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの書き込みに失敗しました", e);
        }
    }

    public boolean exists() {
        return path.toFile().exists();
    }

    public void create() throws IllegalStateException {
        if (exists()) {
            throw new IllegalStateException("既にファイルは存在します");
        }
        else try {
            Files.createFile(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの作成に失敗しました", e);
        }
    }

    public void delete() throws IllegalStateException {
        if (exists()) try {
            Files.delete(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの削除に失敗しました", e);
        }
        else {
            throw new IllegalStateException("ファイルが存在しません");
        }
    }

    public long getSize() throws IllegalStateException {
        try {
            return Files.size(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルサイズの取得に失敗しました", e);
        }
    }

    public @NotNull File getAsFile() {
        return path.toFile();
    }

    public @NotNull JSONObject readAsObject() throws IllegalStateException {
        return JSONParser.parseObject(readAsString());
    }

    public @NotNull JSONArray readAsArray() throws IllegalStateException {
        return JSONParser.parseArray(readAsString());
    }

    public void write(@NotNull JSONStructure structure) throws IllegalStateException {
        writeDirectly(JSONSerializer.serialize(structure));
    }

    public void edit(@NotNull Function<JSONStructure, JSONStructure> function) throws IllegalStateException {
        final String text = readAsString();
        final JSONStructure structure;

        if (JSONParser.isObject(text)) {
            structure = function.apply(JSONParser.parseObject(text));
        }
        else if (JSONParser.isArray(text)) {
            structure = function.apply(JSONParser.parseArray(text));
        }
        else {
            throw new IllegalStateException("JSON文字列として無効です");
        }

        write(structure);
    }
}
