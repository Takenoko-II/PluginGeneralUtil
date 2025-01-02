package com.gmail.subnokoii78.util.file.mojangson;

import com.gmail.subnokoii78.util.file.json.*;
import com.gmail.subnokoii78.util.file.mojangson.values.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MojangsonSerializer {
    private final int indentationSpaceCount;

    private final MojangsonStructure value;

    private MojangsonSerializer(@NotNull MojangsonStructure value, int indentationSpaceCount) {
        this.value = value;
        this.indentationSpaceCount = indentationSpaceCount;
    }

    private @NotNull StringBuilder serialize() throws MojangsonSerializationException {
        return serialize(this.value, 1);
    }

    private @NotNull StringBuilder serialize(Object value, int indentation) throws MojangsonSerializationException {
        return switch (value) {
            case Boolean v -> bool(v);
            case Number v -> number(v);
            case String v -> string(v);
            case MojangsonCompound v -> compound(v, indentation);
            case MojangsonIterable<?> v -> iterable(v, indentation);
            case MojangsonValue<?> v -> serialize(v.value, indentation);
            case null -> new StringBuilder("null");
            default -> throw new JSONSerializeException("このオブジェクトは無効な型の値を含みます");
        };
    }

    private StringBuilder compound(MojangsonCompound compound, int indentation) {
        final String[] keys = compound.keys().toArray(String[]::new);

        final StringBuilder stringBuilder = new StringBuilder().append(OBJECT_BRACE_START);

        for (int i = 0; i < keys.length; i++) {
            final String key = keys[i];

            try {
                final Object childValue = compound.getKey(key, compound.getTypeOfKey(key));
                stringBuilder
                    .append(LINE_BREAK)
                    .append(indentation(indentation + 1))
                    .append(key)
                    .append(COLON)
                    .append(WHITESPACE)
                    .append(serialize(childValue, indentation + 1));
            }
            catch (IllegalArgumentException e) {
                throw new JSONSerializeException("キー'" + key + "における無効な型: " + compound.getTypeOfKey(key), e);
            }

            if (i != keys.length - 1) {
                stringBuilder.append(COMMA);
            }
        }

        if (keys.length > 0) {
            stringBuilder
                .append(LINE_BREAK)
                .append(indentation(indentation));
        }

        stringBuilder.append(OBJECT_BRACE_END);

        return stringBuilder;
    }

    private StringBuilder iterable(@NotNull MojangsonIterable<?> iterable, int indentation) {
        StringBuilder stringBuilder = new StringBuilder().append(ARRAY_BRACE_START);

        if (ITERABLE_TYPE_SYMBOLS.containsKey(iterable.getClass())) {
            stringBuilder
                .append(ITERABLE_TYPE_SYMBOLS.get(iterable.getClass()))
                .append(SEMICOLON);
        }

        int i = 0;
        for (final MojangsonValue<?> element : iterable) {
            if (i >= 1) {
                stringBuilder.append(COMMA);
            }

            try {
                stringBuilder
                    .append(LINE_BREAK)
                    .append(indentation(indentation + 1))
                    .append(serialize(element, indentation + 1));
            }
            catch (IllegalArgumentException e) {
                throw new JSONSerializeException("インデックス'" + i + "における無効な型: " + element.getClass().getName(), e);
            }

            i++;
        }

        if (!iterable.isEmpty()) {
            stringBuilder
                .append(LINE_BREAK)
                .append(indentation(indentation));
        }

        return stringBuilder.append(ARRAY_BRACE_END);
    }

    private StringBuilder string(@NotNull String value) {
        return new StringBuilder(value);
    }

    private StringBuilder bool(boolean value) {
        if (value) return new StringBuilder("true");
        else return new StringBuilder("false");
    }

    private StringBuilder number(@NotNull Number value) {
        final StringBuilder stringBuilder = new StringBuilder(String.valueOf(value));

        if (NUMBER_TYPE_SYMBOLS.containsKey(value.getClass())) {
            stringBuilder.append(NUMBER_TYPE_SYMBOLS.get(value.getClass()));
        }

        return stringBuilder;
    }

    private String indentation(int indentation) {
        return String
            .valueOf(WHITESPACE)
            .repeat(indentationSpaceCount)
            .repeat(indentation - 1);
    }

    private static final char LINE_BREAK = '\n';

    private static final char QUOTE = '"';

    private static final char COLON = ':';

    private static final char COMMA = ',';

    private static final char SEMICOLON = ';';

    private static final Map<Class<? extends MojangsonIterable<?>>, Character> ITERABLE_TYPE_SYMBOLS = new HashMap<>(Map.of(
        MojangsonByteArray.class, 'B',
        MojangsonIntArray.class, 'I',
        MojangsonLongArray.class, 'L'
    ));

    private static final Map<Class<? extends Number>, Character> NUMBER_TYPE_SYMBOLS = new HashMap<>(Map.of(
        byte.class, 'b',
        short.class, 's',
        long.class, 'L',
        float.class, 'f',
        double.class, 'd',
        Byte.class, 'b',
        Short.class, 's',
        Long.class, 'L',
        Float.class, 'f',
        Double.class, 'd'
    ));

    private static final char OBJECT_BRACE_START = '{';

    private static final char OBJECT_BRACE_END = '}';

    private static final char ARRAY_BRACE_START = '[';

    private static final char ARRAY_BRACE_END = ']';

    private static final char WHITESPACE = ' ';

    public static @NotNull String serialize(@NotNull MojangsonStructure structure) {
        return new MojangsonSerializer(structure, 4).serialize().toString();
    }
}
