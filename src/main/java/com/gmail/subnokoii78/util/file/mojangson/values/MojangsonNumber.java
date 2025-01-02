package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueType;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

public class MojangsonNumber<T extends Number> extends MojangsonValue<T> {
    protected MojangsonNumber(@NotNull T value) {
        super(value);
    }

    public final byte byteValue() {
        return value.byteValue();
    }

    public final short shortValue() {
        return value.shortValue();
    }

    public final int intValue() {
        return value.intValue();
    }

    public final long longValue() {
        return value.longValue();
    }

    public final float floatValue() {
        return value.floatValue();
    }

    public final double doubleValue() {
        return value.doubleValue();
    }

    public static @NotNull MojangsonNumber<?> toSubClass(@NotNull Number value) {
        return (MojangsonNumber<?>) MojangsonValueTypes.get(value).cast(value);
    }
}
