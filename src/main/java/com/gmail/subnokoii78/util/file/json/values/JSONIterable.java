package com.gmail.subnokoii78.util.file.json.values;

import com.gmail.subnokoii78.util.file.json.JSONValue;
import org.jetbrains.annotations.NotNull;

public interface JSONIterable<T extends JSONValue<?>> extends Iterable<T> {
    boolean isEmpty();

    @NotNull JSONIterable<T> copy();
}
