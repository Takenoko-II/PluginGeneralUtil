package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MojangsonLongArray extends MojangsonValue<long[]> implements MojangsonStructure, MojangsonIterable<MojangsonLong> {
    public MojangsonLongArray(long[] value) {
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
    public @NotNull Iterator<MojangsonLong> iterator() {
        final List<MojangsonLong> longs = new ArrayList<>();
        for (final long longValue : value) {
            longs.add(MojangsonLong.valueOf(longValue));
        }
        return longs.iterator();
    }

    @Override
    public @NotNull String toString() {
        return "long" + Arrays.toString(value);
    }

    public long[] toLongArray() {
        return Arrays.copyOf(value, value.length);
    }

    public @NotNull MojangsonList toList() {
        final MojangsonList list = new MojangsonList();
        for (final long longValue : value) {
            list.add(longValue);
        }
        return list;
    }

    public static @NotNull MojangsonLongArray toArray(@NotNull MojangsonList list) {
        final long[] longs = new long[list.length()];

        for (int i = 0; i < list.length(); i++) {
            if (!list.getTypeAt(i).equals(MojangsonValueTypes.LONG)) {
                throw new IllegalArgumentException("キャストに失敗しました");
            }

            longs[i] = list.get(i, MojangsonValueTypes.LONG).longValue();
        }

        return new MojangsonLongArray(longs);
    }
}
