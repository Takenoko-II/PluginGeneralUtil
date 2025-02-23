package com.gmail.subnokoii78.util.file.json;

import com.gmail.subnokoii78.util.file.json.values.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class JSONValueTypes {
    private JSONValueTypes() {}

    public static @NotNull JSONValueType<?> get(Object value) {
        return switch (value) {
            case JSONValue<?> jsonValue -> get(jsonValue.value);
            case Boolean ignored -> BOOLEAN;
            case Number ignored -> NUMBER;
            case String ignored -> STRING;
            case Map<?, ?> ignored -> OBJECT;
            case Collection<?> ignored -> ARRAY;
            case Character ignored -> STRING;
            case null -> NULL;
            default -> {
                if (value.getClass().isArray()) yield ARRAY;
                else throw new IllegalArgumentException("渡された値はjsonで使用できない型です: " + value.getClass().getName());
            }
        };
    }

    public static final @NotNull JSONValueType<JSONBoolean> BOOLEAN = new JSONValueType<>(JSONBoolean.class) {
        @Override
        public JSONBoolean cast(Object value) {
            if (value instanceof JSONBoolean v) return v;
            else if (value instanceof Boolean v) return JSONBoolean.valueOf(v);
            else throw new IllegalArgumentException("value is not a boolean value");
        }
    };

    public static final @NotNull JSONValueType<JSONNumber> NUMBER = new JSONValueType<>(JSONNumber.class) {
        @Override
        public JSONNumber cast(Object value) {
            if (value instanceof JSONNumber v) return v;
            else if (value instanceof Number v) return JSONNumber.valueOf(v);
            else throw new IllegalArgumentException("value is not a number value");
        }
    };

    public static final @NotNull JSONValueType<JSONString> STRING = new JSONValueType<>(JSONString.class) {
        @Override
        public JSONString cast(Object value) {
            return switch (value) {
                case JSONString v -> v;
                case String v -> JSONString.valueOf(v);
                case Character v -> JSONString.valueOf(String.valueOf(v));
                case null, default -> throw new IllegalArgumentException("value is not a string value");
            };
        }

        @Override
        public String toString() {
            return "String";
        }
    };

    public static final @NotNull JSONValueType<JSONObject> OBJECT = new JSONValueType<>(JSONObject.class) {
        @Override
        public JSONObject cast(Object value) {
            if (value instanceof JSONObject jsonObject) return jsonObject;

            if (value instanceof Map<?, ?> map) {
                final Map<String, JSONValue<?>> object = new HashMap<>();

                for (final Object key : map.keySet()) {
                    if (key instanceof String string) {
                        final Object val = map.get(string);
                        object.put(string, get(val).cast(val));
                    }
                    else {
                        throw new IllegalArgumentException("A key of Map is not a string");
                    }
                }

                return new JSONObject(object);
            }
            else throw new IllegalArgumentException("value is not a json object value: " + value.getClass().getName());
        }
    };

    public static final @NotNull JSONValueType<JSONArray> ARRAY = new JSONValueType<>(JSONArray.class) {
        @Override
        public JSONArray cast(Object value) {
            switch (value) {
                case JSONArray jsonArray -> {
                    return jsonArray;
                }
                case Iterable<?> iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case Object[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case boolean[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case byte[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case short[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case int[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case long[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case char[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case float[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case double[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(get(element).cast(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case null -> {
                    throw new IllegalArgumentException("value is not a json array value: null");
                }
                default -> throw new IllegalArgumentException("value is not a json array value: " + value.getClass().getName());
            }
        }
    };

    public static final @NotNull JSONValueType<JSONNull> NULL = new JSONValueType<>(JSONNull.class) {
        @Override
        public JSONNull cast(Object value) {
            if (value instanceof JSONNull jsonNull) return jsonNull;
            else if (value == null) return JSONNull.NULL;
            else throw new IllegalArgumentException("value is not a null value");
        }
    };
}
