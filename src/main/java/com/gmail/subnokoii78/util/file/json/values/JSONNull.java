package com.gmail.subnokoii78.util.file.json.values;

public final class JSONNull extends JSONPrimitive<Object> {
    private JSONNull() {
        super(null);
    }

    @Override
    public String toString() {
        return "null";
    }

    public static final JSONNull NULL = new JSONNull();
}
