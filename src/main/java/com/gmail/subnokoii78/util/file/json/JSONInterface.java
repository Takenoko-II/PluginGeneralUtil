package com.gmail.subnokoii78.util.file.json;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonParser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JSONInterface {
    private static final Map<String, JSONValueType<?>> types = new HashMap<>(Map.of(
        "boolean", JSONValueType.BOOLEAN,
        "number", JSONValueType.NUMBER,
        "string", JSONValueType.STRING,
        "object", JSONValueType.OBJECT,
        "array", JSONValueType.ARRAY,
        "null", JSONValueType.NULL
    ));

    private final JSONObject struct;

    public JSONInterface(@NotNull String mojangson) {
        struct = new JSONObject(MojangsonParser.compound(mojangson));
        checkIsValidObject(struct);
    }

    private JSONInterface(@NotNull JSONObject jsonObject) {
        struct = jsonObject;
        checkIsValidObject(struct);
    }

    public boolean matches(@NotNull JSONObject jsonObject) {
        return matchObject(struct, jsonObject);
    }

    private static boolean matchObject(@NotNull JSONObject struct, @NotNull JSONObject jsonObject) {
        for (final String key : struct.keys()) {
            if (!jsonObject.hasKey(key)) {
                return false;
            }

            final Object value = struct.get(key, struct.getTypeOfKey(key));

            if (value instanceof String id) {
                if (!types.get(id).equals(jsonObject.getTypeOfKey(key))) {
                    return false;
                }
            }
            else if (value instanceof JSONObject object) {
                if (!jsonObject.getTypeOfKey(key).equals(JSONValueType.OBJECT)) {
                    return false;
                }
                if (!matchObject(object, jsonObject.get(key, JSONValueType.OBJECT))) {
                    return false;
                }
            }
            else if (value instanceof JSONArray array) {
                if (!jsonObject.getTypeOfKey(key).equals(JSONValueType.ARRAY)) {
                    return false;
                }
                if (!matchArray(array, jsonObject.get(key, JSONValueType.ARRAY))) {
                    return false;
                }
            }
            else {
                throw new IllegalArgumentException("無効な型の値です");
            }
        }

        return true;
    }

    private static boolean matchArray(@NotNull JSONArray struct, @NotNull JSONArray jsonArray) {
        for (int i = 0; i < struct.length(); i++) {
            final Object element = struct.get(i, struct.getTypeAt(i));

            if (element instanceof String id) {
                if (!types.get(id).equals(JSONValueType.of(element))) {
                    return false;
                }
            }
            else if (element instanceof JSONObject object) {
                if (!struct.getTypeAt(i).equals(JSONValueType.OBJECT)) {
                    return false;
                }
                else if (!matchObject(object, struct.get(i, JSONValueType.OBJECT))) {
                    return false;
                }
            }
            else if (element instanceof JSONArray array) {
                if (!jsonArray.getTypeAt(i).equals(struct.getTypeAt(i))) {
                    return false;
                }
                if (!matchArray(array, jsonArray.get(i, JSONValueType.ARRAY))) {
                    return false;
                }
            }
            else {
                throw new IllegalArgumentException("無効な型の要素です");
            }
        }

        return true;
    }

    private static void checkIsValidObject(@NotNull JSONObject struct) {
        for (final String key : struct.keys()) {
            final Object value = struct.get(key, struct.getTypeOfKey(key));

            if (value instanceof String string) {
                if (!types.containsKey(string)) {
                    throw new IllegalArgumentException("型として無効なIDです");
                }
            }
            else if (value instanceof JSONObject jsonObject) {
                checkIsValidObject(jsonObject);
            }
            else if (value instanceof JSONArray jsonArray) {
                checkIsValidArray(jsonArray);
            }
        }
    }

    private static void checkIsValidArray(@NotNull JSONArray struct) {
        for (final Object element : struct) {
            if (element instanceof String string) {
                if (!types.containsKey(string)) {
                    throw new IllegalArgumentException("型として無効なIDです");
                }
            }
            else if (element instanceof JSONObject jsonObject) {
                checkIsValidObject(jsonObject);
            }
            else if (element instanceof JSONArray jsonArray) {
                checkIsValidArray(jsonArray);
            }
        }
    }

    public static void main(String[] args) {
        final JSONInterface jsonInterface = new JSONInterface("{foo:string,bar:number,baz:boolean,struct:{test:string}}");

        System.out.println(jsonInterface.matches(
            JSONParser.parseObject("""
                {
                    "foo": "s",
                    "bar": 0,
                    "baz": false,
                    "struct": {
                        "test": "hoge"
                    }
                }
            """)
        ));
    }
}
