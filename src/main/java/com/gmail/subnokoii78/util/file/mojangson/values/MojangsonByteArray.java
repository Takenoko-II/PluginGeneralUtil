package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MojangsonByteArray extends MojangsonValue<byte[]> implements MojangsonStructure, MojangsonIterable<MojangsonByte> {
    public MojangsonByteArray(byte[] value) {
        super(value);
    }

    @Override
    public @NotNull MojangsonStructure copy() {
        return toArray(toList());
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public @NotNull Iterator<MojangsonByte> iterator() {
        final List<MojangsonByte> bytes = new ArrayList<>();
        for (final byte byteValue : value) {
            bytes.add(MojangsonByte.valueOf(byteValue));
        }
        return bytes.iterator();
    }

    @Override
    public @NotNull String toString() {
        return "byte" + Arrays.toString(value);
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(value, value.length);
    }

    public @NotNull MojangsonList toList() {
        final MojangsonList list = new MojangsonList();
        for (final byte byteValue : value) {
            list.add(byteValue);
        }
        return list;
    }

    public static @NotNull MojangsonByteArray toArray(@NotNull MojangsonList list) {
        final byte[] bytes = new byte[list.length()];

        for (int i = 0; i < list.length(); i++) {
            if (!list.getTypeAt(i).equals(MojangsonValueTypes.BYTE)) {
                throw new IllegalArgumentException("キャストに失敗しました");
            }

            bytes[i] = list.get(i, MojangsonValueTypes.BYTE).byteValue();
        }

        return new MojangsonByteArray(bytes);
    }
}
