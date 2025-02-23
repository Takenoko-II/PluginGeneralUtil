package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import org.jetbrains.annotations.NotNull;

public interface MojangsonIterable<T extends MojangsonValue<?>> extends Iterable<T> {
    boolean isEmpty();

    @NotNull MojangsonIterable<T> copy();
}
