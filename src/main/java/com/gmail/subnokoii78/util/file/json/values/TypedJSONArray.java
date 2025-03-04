package com.gmail.subnokoii78.util.file.json.values;

import com.gmail.subnokoii78.util.file.json.JSONValue;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import com.gmail.subnokoii78.util.file.json.JSONValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypedJSONArray<T extends JSONValue<?>> extends JSONValue<List<T>> implements JSONIterable<T>, JSONStructure {
    private final JSONValueType<T> type;

    public TypedJSONArray(@NotNull JSONValueType<T> type) {
        super(new ArrayList<>());
        this.type = type;
    }

    public TypedJSONArray(@NotNull JSONValueType<T> type, @NotNull List<T> list) {
        super(new ArrayList<>(list));
        this.type = type;
    }

    public boolean has(int index) {
        return index < value.size();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    protected boolean checkTypeAt(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        return JSONValueTypes.get(value.get(index)).equals(type);
    }

    public T get(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!checkTypeAt(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        return value.get(index);
    }

    public void add(int index, T value) {
        if (index < 0 || index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        this.value.add(index, value);
    }

    public void add(T value) {
        this.value.add(value);
    }

    public void set(int index, T value) {
        if (index < 0 || index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        this.value.set(index, value);
    }

    public void delete(int index) {
        if (has(index)) value.remove(index);
    }

    public void clear() {
        value.clear();
    }

    public int length() {
        return value.size();
    }

    @Override
    public @NotNull TypedJSONArray<T> copy() {
        return new TypedJSONArray<>(type, value);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        final List<T> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i));
        }

        return list.iterator();
    }

    public @NotNull JSONArray untyped() {
        final JSONArray array = new JSONArray();
        for (int i = 0; i < length(); i++) {
            array.add(get(i));
        }
        return array;
    }
}
