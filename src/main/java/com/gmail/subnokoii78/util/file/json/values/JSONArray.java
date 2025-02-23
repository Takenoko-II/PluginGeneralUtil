package com.gmail.subnokoii78.util.file.json.values;

import com.gmail.subnokoii78.util.file.json.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JSONArray extends JSONValue<List<JSONValue<?>>> implements JSONIterable<JSONValue<?>>, JSONStructure {
    public JSONArray() {
        super(new ArrayList<>());
    }

    public JSONArray(@NotNull List<JSONValue<?>> list) {
        super(new ArrayList<>(list));
    }

    public boolean has(int index) {
        return index < value.size();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public JSONValueType<?> getTypeAt(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        return JSONValueTypes.get(value.get(index));
    }

    public <T extends JSONValue<?>> T get(int index, @NotNull JSONValueType<T> type) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        return type.cast(value.get(index));
    }

    public <T> @NotNull T get(int index, @NotNull JSONValueConverter<T> converter) {
        return converter.convert(get(index, getTypeAt(index)));
    }

    public void add(int index, Object value) {
        if (index < 0 || index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        this.value.add(index, JSONValueTypes.get(value).cast(value));
    }

    public void add(Object value) {
        this.value.add(JSONValueTypes.get(value).cast(value));
    }

    public void set(int index, Object value) {
        if (index < 0 || index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        this.value.set(index, JSONValueTypes.get(value).cast(value));
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
    public @NotNull Iterator<JSONValue<?>> iterator() {
        final List<JSONValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    public List<Object> asList() {
        final List<Object> list = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final JSONValueType<?> type = getTypeAt(i);

            if (type.equals(JSONValueTypes.OBJECT)) {
                final JSONObject object = get(i, JSONValueTypes.OBJECT);
                list.add(object.asMap());
            }
            else if (type.equals(JSONValueTypes.ARRAY)) {
                final JSONArray array = get(i, JSONValueTypes.ARRAY);
                list.add(array.asList());
            }
            else {
                list.add(value.get(i));
            }
        }

        return list;
    }

    @Override
    public @NotNull JSONArray copy() {
        return JSONValueTypes.ARRAY.cast(asList());
    }

    public boolean isArrayOf(@NotNull JSONValueType<?> type) {
        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                return false;
            }
        }

        return true;
    }

    public <T extends JSONValue<?>> TypedJSONArray<T> typed(@NotNull JSONValueType<T> type) {
        final TypedJSONArray<T> array = new TypedJSONArray<>(type);

        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                throw new IllegalStateException("その型の値でない要素が見つかりました: " + getTypeAt(i).toString());
            }

            final T element = get(i, type);
            array.add(element);
        }

        return array;
    }
}
