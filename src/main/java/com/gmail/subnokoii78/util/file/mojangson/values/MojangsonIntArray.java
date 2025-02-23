package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MojangsonIntArray extends MojangsonValue<int[]> implements MojangsonStructure, MojangsonIterable<MojangsonInt> {
    public MojangsonIntArray(int[] value) {
        super(value);
    }

    @Override
    public @NotNull MojangsonIntArray copy() {
        return toArray(toList());
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public @NotNull Iterator<MojangsonInt> iterator() {
        final List<MojangsonInt> bytes = new ArrayList<>();
        for (final int intValue : value) {
            bytes.add(MojangsonInt.valueOf(intValue));
        }
        return bytes.iterator();
    }

    @Override
    public @NotNull String toString() {
        return "int" + Arrays.toString(value);
    }

    public int[] toIntArray() {
        return Arrays.copyOf(value, value.length);
    }

    public @NotNull MojangsonList toList() {
        final MojangsonList list = new MojangsonList();
        for (final int intValue : value) {
            list.add(intValue);
        }
        return list;
    }

    public static @NotNull MojangsonIntArray toArray(@NotNull MojangsonList list) {
        final int[] ints = new int[list.length()];

        for (int i = 0; i < list.length(); i++) {
            if (!list.getTypeAt(i).equals(MojangsonValueTypes.INT)) {
                throw new IllegalArgumentException("キャストに失敗しました");
            }

            ints[i] = list.get(i, MojangsonValueTypes.INT).intValue();
        }

        return new MojangsonIntArray(ints);
    }
}
