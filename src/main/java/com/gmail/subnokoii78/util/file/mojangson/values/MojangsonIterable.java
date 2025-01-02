package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;

public interface MojangsonIterable<T extends MojangsonValue<?>> extends Iterable<T> {
    boolean isEmpty();
}
