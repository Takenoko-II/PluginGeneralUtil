package com.gmail.subnokoii78.util.file.json.values;

import com.gmail.subnokoii78.util.file.json.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class JSONObject extends JSONValue<Map<String, JSONValue<?>>> implements JSONStructure {
    private final JSONPathAccessor accessor = new JSONPathAccessor(this);

    public JSONObject() {
        super(new HashMap<>());
    }

    public JSONObject(@NotNull Map<String, JSONValue<?>> map) {
        super(map);
    }

    public boolean hasKey(@NotNull String key) {
        return value.containsKey(key);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public @NotNull JSONValueType<?> getTypeOfKey(@NotNull String key) {
        if (!hasKey(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return JSONValueTypes.get(value.get(key));
    }

    public @NotNull <T extends JSONValue<?>> T getKey(@NotNull String key, JSONValueType<T> type) {
        if (!hasKey(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOfKey(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません");
        }

        return type.cast(value.get(key));
    }

    public void setKey(@NotNull String key, Object value) {
        this.value.put(key, JSONValueTypes.get(value).cast(value));
    }

    public void deleteKey(@NotNull String key) {
        if (hasKey(key)) value.remove(key);
    }

    public void clear() {
        value.clear();
    }

    public Set<String> keys() {
        return value.keySet();
    }

    public void merge(@NotNull JSONObject jsonObject) {
        for (String key : jsonObject.keys()) {
            setKey(key, jsonObject.value.get(key));
        }
    }

    public Map<String, Object> asMap() {
        final Map<String, Object> map = new HashMap<>();

        for (String key : keys()) {
            final JSONValueType<?> type = getTypeOfKey(key);

            if (type.equals(JSONValueTypes.OBJECT)) {
                final JSONObject object = getKey(key, JSONValueTypes.OBJECT);
                map.put(key, object.asMap());
            }
            else if (type.equals(JSONValueTypes.ARRAY)) {
                final JSONArray array = getKey(key, JSONValueTypes.ARRAY);
                map.put(key, array.asList());
            }
            else if (value.get(key) instanceof JSONPrimitive<?> primitive) {
                map.put(key, primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(key).getClass().getName());
            }
        }

        return map;
    }

    @Override
    public @NotNull JSONObject copy() {
        return JSONValueTypes.OBJECT.cast(asMap());
    }

    public boolean has(@NotNull String path) {
        return accessor.has(path);
    }

    public @NotNull JSONValueType<?> getTypeOf(@NotNull String path) {
        return accessor.getTypeOf(path);
    }

    public <T extends JSONValue<?>> @NotNull T get(@NotNull String path, @NotNull JSONValueType<T> type) {
        return accessor.get(path, type);
    }

    public <T extends JSONValue<?>> @NotNull T get(@NotNull String path, @NotNull JSONValueConverter<T> converter) {
        final T v = accessor.access(path, false, accessor -> {
            final Object value = accessor.get(accessor.getType());
            return converter.convert(value);
        });

        if (v == null) {
            throw new IllegalArgumentException("パスが存在しません");
        }

        return v;
    }

    public void set(@NotNull String path, Object value) {
        accessor.set(path, value);
    }

    public void delete(@NotNull String path) {
        accessor.delete(path);
    }
}
