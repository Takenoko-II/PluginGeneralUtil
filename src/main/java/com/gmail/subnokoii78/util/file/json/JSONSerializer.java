package com.gmail.subnokoii78.util.file.json;

import com.gmail.subnokoii78.util.file.json.values.JSONArray;
import com.gmail.subnokoii78.util.file.json.values.JSONObject;
import com.gmail.subnokoii78.util.file.json.values.JSONStructure;
import com.gmail.subnokoii78.util.file.json.values.TypedJSONArray;
import org.jetbrains.annotations.NotNull;

public final class JSONSerializer {
    private final int indentationSpaceCount;

    private final JSONStructure value;

    private JSONSerializer(@NotNull JSONStructure value, int indentationSpaceCount) {
        this.value = value;
        this.indentationSpaceCount = indentationSpaceCount;
    }

    private StringBuilder serialize() throws JSONSerializeException {
        return serialize(this.value, 1);
    }

    private StringBuilder serialize(Object value, int indentation) throws JSONSerializeException {
        return switch (value) {
            case Boolean v -> bool(v);
            case Number v -> number(v);
            case String v -> string(v);
            case JSONObject v -> object(v, indentation);
            case JSONArray v -> array(v, indentation);
            case TypedJSONArray<?> v -> array(v.untyped(), indentation);
            case JSONValue<?> v -> serialize(v.value, indentation);
            case null -> new StringBuilder("null");
            default -> throw new JSONSerializeException("このオブジェクトは無効な型の値を含みます");
        };
    }

    private StringBuilder object(JSONObject value, int indentation) {
        final String[] keys = value.keys().toArray(String[]::new);

        final StringBuilder stringBuilder = new StringBuilder().append(OBJECT_BRACE_START);

        for (int i = 0; i < keys.length; i++) {
            final String key = keys[i];

            try {
                final Object childValue = value.getKey(key, value.getTypeOfKey(key));
                stringBuilder
                    .append(LINE_BREAK)
                    .append(indentation(indentation + 1))
                    .append(QUOTE)
                    .append(key)
                    .append(QUOTE)
                    .append(COLON)
                    .append(WHITESPACE)
                    .append(serialize(childValue, indentation + 1));
            }
            catch (IllegalArgumentException e) {
                throw new JSONSerializeException("キー'" + key + "における無効な型: " + value.getTypeOfKey(key), e);
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

    private StringBuilder array(@NotNull JSONArray array, int indentation) {
        StringBuilder stringBuilder = new StringBuilder().append(ARRAY_BRACE_START);

        for (int i = 0; i < array.length(); i++) {
            try {
                final Object element = array.get(i, array.getTypeAt(i));
                stringBuilder
                    .append(LINE_BREAK)
                    .append(indentation(indentation + 1))
                    .append(serialize(element, indentation + 1));
            }
            catch (IllegalArgumentException e) {
                throw new JSONSerializeException("インデックス'" + i + "における無効な型: " + array.getTypeAt(i), e);
            }

            if (i != array.length() - 1) {
                stringBuilder.append(COMMA);
            }
        }

        if (!array.isEmpty()) {
            stringBuilder
                .append(LINE_BREAK)
                .append(indentation(indentation));
        }

        return stringBuilder.append(ARRAY_BRACE_END);
    }

    private StringBuilder string(@NotNull String value) {
        return new StringBuilder()
            .append(QUOTE)
            .append(value.replaceAll("\"", "\\\\\""))
            .append(QUOTE);
    }

    private StringBuilder bool(boolean value) {
        if (value) return new StringBuilder("true");
        else return new StringBuilder("false");
    }

    private StringBuilder number(@NotNull Number value) {
        return new StringBuilder(String.valueOf(value));
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

    private static final char OBJECT_BRACE_START = '{';

    private static final char OBJECT_BRACE_END = '}';

    private static final char ARRAY_BRACE_START = '[';

    private static final char ARRAY_BRACE_END = ']';

    private static final char WHITESPACE = ' ';

    public static @NotNull String serialize(@NotNull JSONStructure structure) {
        return new JSONSerializer(structure, 4).serialize().toString();
    }
}
