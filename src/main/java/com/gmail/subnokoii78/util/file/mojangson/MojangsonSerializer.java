package com.gmail.subnokoii78.util.file.mojangson;

import com.gmail.subnokoii78.util.file.json.*;
import com.gmail.subnokoii78.util.file.mojangson.values.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MojangsonSerializer {
    private final int indentationSpaceCount;

    private final boolean asJsonString;

    private final MojangsonStructure value;

    private MojangsonSerializer(@NotNull MojangsonStructure value, int indentationSpaceCount, boolean asJsonString) {
        this.value = value;
        this.indentationSpaceCount = indentationSpaceCount;
        this.asJsonString = asJsonString;
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

        final StringBuilder stringBuilder = new StringBuilder().append(COMPOUND_BRACES[0]);

        for (int i = 0; i < keys.length; i++) {
            final String key = keys[i];

            try {
                final Object childValue = compound.getKey(key, compound.getTypeOfKey(key));
                stringBuilder
                    .append(LINE_BREAK)
                    .append(indentation(indentation + 1))
                    .append(string(key))
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

        stringBuilder.append(COMPOUND_BRACES[1]);

        return stringBuilder;
    }

    private StringBuilder iterable(@NotNull MojangsonIterable<?> iterable, int indentation) {
        StringBuilder stringBuilder = new StringBuilder().append(ARRAY_LIST_BRACES[0]);

        if (!asJsonString && ITERABLE_TYPE_SYMBOLS.containsKey(iterable.getClass())) {
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

        return stringBuilder.append(ARRAY_LIST_BRACES[1]);
    }

    private StringBuilder string(@NotNull String value) {
        boolean requireQuote = asJsonString || SYMBOLS_ON_STRING.stream().anyMatch(sym -> value.contains(sym.toString()));
        final StringBuilder stringBuilder = new StringBuilder();

        if (requireQuote) stringBuilder.append(QUOTE);
        stringBuilder.append(value.replaceAll(String.valueOf(QUOTE), String.valueOf(ESCAPE).repeat(2) + QUOTE));
        if (requireQuote) stringBuilder.append(QUOTE);

        return stringBuilder;
    }

    private StringBuilder bool(boolean value) {
        if (value) return new StringBuilder("true");
        else return new StringBuilder("false");
    }

    private StringBuilder number(@NotNull Number value) {
        final StringBuilder stringBuilder = new StringBuilder(String.valueOf(value));

        if (!asJsonString && NUMBER_TYPE_SYMBOLS.containsKey(value.getClass())) {
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

    private static final char[] COMPOUND_BRACES = {'{', '}'};

    private static final char[] ARRAY_LIST_BRACES = {'[', ']'};

    private static final char WHITESPACE = ' ';

    private static final char ESCAPE = '\\';

    private static final Set<Character> SIGNS = Set.of('+', '-');

    private static final char DECIMAL_POINT = '.';

    private static final Set<Character> SYMBOLS_ON_STRING = new HashSet<>();

    static {
        SYMBOLS_ON_STRING.add(WHITESPACE);
        SYMBOLS_ON_STRING.add(LINE_BREAK);
        SYMBOLS_ON_STRING.add(COMMA);
        SYMBOLS_ON_STRING.add(COLON);
        SYMBOLS_ON_STRING.add(SEMICOLON);
        SYMBOLS_ON_STRING.add(ESCAPE);
        SYMBOLS_ON_STRING.add(QUOTE);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[0]);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[1]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[0]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[1]);
        SYMBOLS_ON_STRING.addAll(SIGNS);
        SYMBOLS_ON_STRING.add(DECIMAL_POINT);
    }

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

    public static @NotNull String serialize(@NotNull MojangsonStructure structure) {
        return new MojangsonSerializer(structure, 4, false).serialize().toString();
    }

    public static @NotNull String toJson(MojangsonStructure structure) {
        return new MojangsonSerializer(structure, 4, true).serialize().toString();
    }
}
