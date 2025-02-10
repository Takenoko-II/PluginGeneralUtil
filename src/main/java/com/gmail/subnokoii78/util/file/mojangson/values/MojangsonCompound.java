package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonPathAccessor;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueType;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MojangsonCompound extends MojangsonValue<Map<String, MojangsonValue<?>>> implements MojangsonStructure {
    private final MojangsonPathAccessor accessor = MojangsonPathAccessor.newAccessor(this);

    public MojangsonCompound(@NotNull Map<String, MojangsonValue<?>> value) {
        super(value);
    }

    public MojangsonCompound() {
        this(new HashMap<>());
    }

    public boolean hasKey(@NotNull String key) {
        return value.containsKey(key);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public @NotNull MojangsonValueType<?> getTypeOfKey(@NotNull String key) {
        if (!hasKey(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return MojangsonValueTypes.get(value.get(key));
    }

    public @NotNull <T extends MojangsonValue<?>> T getKey(@NotNull String key, MojangsonValueType<T> type) {
        if (!hasKey(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOfKey(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません");
        }

        return type.cast(value.get(key));
    }

    public void setKey(@NotNull String key, Object value) {
        this.value.put(key, MojangsonValueTypes.get(value).cast(value));
    }

    public void deleteKey(@NotNull String key) {
        if (hasKey(key)) value.remove(key);
    }

    public void clear() {
        value.clear();
    }

    public @NotNull Set<String> keys() {
        return Set.copyOf(value.keySet());
    }

    public @NotNull Map<String, Object> asRawMap() {
        final Map<String, Object> map = new HashMap<>();

        for (final String key : keys()) {
            final MojangsonValueType<?> type = getTypeOfKey(key);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = getKey(key, MojangsonValueTypes.COMPOUND);
                map.put(key, compound.asRawMap());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = getKey(key, MojangsonValueTypes.LIST);
                map.put(key, list.asRawList());
            }
            else {
                map.put(key, value.get(key));
            }
        }

        return map;
    }

    @Override
    public @NotNull MojangsonStructure copy() {
        return MojangsonValueTypes.COMPOUND.cast(asRawMap());
    }

    public boolean has(@NotNull String path) {
        return accessor.has(path);
    }

    public @NotNull MojangsonValueType<?> getTypeOf(@NotNull String path) {
        return accessor.getTypeOf(path);
    }

    public <T extends MojangsonValue<?>> @NotNull T get(@NotNull String path, @NotNull MojangsonValueType<T> type) {
        return accessor.get(path, type);
    }

    public void set(@NotNull String path, Object value) {
        accessor.set(path, value);
    }

    public void delete(@NotNull String path) {
        accessor.delete(path);
    }
}
