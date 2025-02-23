package com.gmail.subnokoii78.util.file.json;

import com.gmail.subnokoii78.util.file.json.values.JSONArray;
import com.gmail.subnokoii78.util.file.json.values.JSONObject;
import com.gmail.subnokoii78.util.file.json.values.JSONStructure;
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

    public @NotNull Path getPath() {
        return path;
    }

    protected @NotNull String readFromFile() throws IllegalStateException {
        if (exists()) try {
            return String.join("\n", Files.readAllLines(path));
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの読み取りに失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    protected void writeToFile(@NotNull String json) throws IllegalStateException {
        if (exists()) try {
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
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public boolean exists() {
        return path.toFile().exists();
    }

    public void create() throws IllegalStateException {
        if (exists()) throw new IllegalStateException("既にファイルは存在します");
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
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public long getSize() throws IllegalStateException {
        if (exists()) try {
            return Files.size(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルサイズの取得に失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public @NotNull JSONStructure read() throws JSONParseException, IllegalStateException {
        return JSONParser.structure(readFromFile());
    }

    public void write(@NotNull JSONStructure structure) throws JSONSerializeException, IllegalStateException {
        writeToFile(JSONSerializer.serialize(structure));
    }

    public @NotNull JSONObject readAsObject() throws JSONParseException, IllegalStateException {
        return JSONParser.object(readFromFile());
    }

    public @NotNull JSONArray readAsArray() throws JSONParseException, IllegalStateException {
        return JSONParser.array(readFromFile());
    }

    public void edit(@NotNull Function<JSONStructure, JSONStructure> function) throws JSONParseException, JSONSerializeException, IllegalStateException {
        write(function.apply(read()));
    }
}
