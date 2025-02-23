package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueType;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MojangsonList extends MojangsonValue<List<MojangsonValue<?>>> implements MojangsonStructure, MojangsonIterable<MojangsonValue<?>> {
    public MojangsonList(@NotNull List<MojangsonValue<?>> value) {
        super(value);
    }

    public MojangsonList() {
        this(new ArrayList<>());
    }

    public boolean has(int index) {
        return index < value.size();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public @NotNull MojangsonValueType<?> getTypeAt(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        return MojangsonValueTypes.get(value.get(index));
    }

    public <T extends MojangsonValue<?>> T get(int index, @NotNull MojangsonValueType<T> type) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        return type.cast(value.get(index));
    }

    public void add(int index, Object value) {
        if (index < 0 || index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        this.value.add(index, MojangsonValueTypes.get(value).cast(value));
    }

    public void add(Object value) {
        this.value.add(MojangsonValueTypes.get(value).cast(value));
    }

    public void set(int index, Object value) {
        if (index < 0 || index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        this.value.set(index, MojangsonValueTypes.get(value).cast(value));
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
    public @NotNull Iterator<MojangsonValue<?>> iterator() {
        final List<MojangsonValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    public @NotNull List<Object> asRawList() {
        final List<Object> arrayList = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final MojangsonValueType<?> type = getTypeAt(i);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = get(i, MojangsonValueTypes.COMPOUND);
                arrayList.add(compound.asRawMap());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = get(i, MojangsonValueTypes.LIST);
                list.add(list.asRawList());
            }
            else {
                arrayList.add(value.get(i));
            }
        }

        return arrayList;
    }

    @Override
    public @NotNull MojangsonList copy() {
        return MojangsonValueTypes.LIST.cast(asRawList());
    }
}
