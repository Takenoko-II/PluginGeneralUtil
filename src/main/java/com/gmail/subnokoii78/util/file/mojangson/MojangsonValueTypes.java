package com.gmail.subnokoii78.util.file.mojangson;

import com.gmail.subnokoii78.util.file.mojangson.values.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MojangsonValueTypes {
    private MojangsonValueTypes() {}

    public static final MojangsonValueType<MojangsonByte> BYTE = new MojangsonValueType<>(MojangsonByte.class) {
        @Override
        public MojangsonByte cast(Object value) {
            if (value instanceof MojangsonByte mojangsonByte) return mojangsonByte;
            else if (value instanceof Byte byteValue) return MojangsonByte.valueOf(byteValue);
            else throw new IllegalArgumentException("byte型でない値はMojangsonByteに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonShort> SHORT = new MojangsonValueType<>(MojangsonShort.class) {
        @Override
        public MojangsonShort cast(Object value) {
            if (value instanceof MojangsonShort mojangsonShort) return mojangsonShort;
            else if (value instanceof Short shortValue) return MojangsonShort.valueOf(shortValue);
            else throw new IllegalArgumentException("short型でない値はMojangsonShortに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonInt> INT = new MojangsonValueType<>(MojangsonInt.class) {
        @Override
        public MojangsonInt cast(Object value) {
            if (value instanceof MojangsonInt mojangsonInt) return mojangsonInt;
            else if (value instanceof Integer intValue) return MojangsonInt.valueOf(intValue);
            else throw new IllegalArgumentException("int型でない値はMojangsonIntに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonLong> LONG = new MojangsonValueType<>(MojangsonLong.class) {
        @Override
        public MojangsonLong cast(Object value) {
            if (value instanceof MojangsonLong mojangsonLong) return mojangsonLong;
            else if (value instanceof Long longValue) return MojangsonLong.valueOf(longValue);
            else throw new IllegalArgumentException("long型でない値はMojangsonLongに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonFloat> FLOAT = new MojangsonValueType<>(MojangsonFloat.class) {
        @Override
        public MojangsonFloat cast(Object value) {
            if (value instanceof MojangsonFloat mojangsonFloat) return mojangsonFloat;
            else if (value instanceof Float floatValue) return MojangsonFloat.valueOf(floatValue);
            else throw new IllegalArgumentException("float型でない値はMojangsonFloatに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonDouble> DOUBLE = new MojangsonValueType<>(MojangsonDouble.class) {
        @Override
        public MojangsonDouble cast(Object value) {
            if (value instanceof MojangsonDouble mojangsonDouble) return mojangsonDouble;
            else if (value instanceof Double doubleValue) return MojangsonDouble.valueOf(doubleValue);
            else throw new IllegalArgumentException("double型でない値はMojangsonDoubleに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonString> STRING = new MojangsonValueType<>(MojangsonString.class) {
        @Override
        public MojangsonString cast(Object value) {
            return switch (value) {
                case MojangsonString mojangsonString -> mojangsonString;
                case String stringValue -> MojangsonString.valueOf(stringValue);
                case Character characterValue -> MojangsonString.valueOf(characterValue);
                case null, default ->
                    throw new IllegalArgumentException("String型でない値はMojangsonStringに変換できません");
            };
        }
    };

    public static final MojangsonValueType<MojangsonByteArray> BYTE_ARRAY = new MojangsonValueType<>(MojangsonByteArray.class) {
        @Override
        public MojangsonByteArray cast(Object value) {
            if (value instanceof MojangsonByteArray mojangsonByteArray) return mojangsonByteArray;
            else if (value instanceof byte[] bytes) return new MojangsonByteArray(bytes);
            else throw new IllegalArgumentException("byte[]型でない値はMojangsonByteArrayに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonIntArray> INT_ARRAY = new MojangsonValueType<>(MojangsonIntArray.class) {
        @Override
        public MojangsonIntArray cast(Object value) {
            if (value instanceof MojangsonIntArray mojangsonIntArray) return mojangsonIntArray;
            else if (value instanceof int[] ints) return new MojangsonIntArray(ints);
            else throw new IllegalArgumentException("int[]型でない値はMojangsonIntArrayに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonLongArray> LONG_ARRAY = new MojangsonValueType<>(MojangsonLongArray.class) {
        @Override
        public MojangsonLongArray cast(Object value) {
            if (value instanceof MojangsonLongArray mojangsonLongArray) return mojangsonLongArray;
            else if (value instanceof long[] longs) return new MojangsonLongArray(longs);
            else throw new IllegalArgumentException("long[]型でない値はMojangsonLongArrayに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonCompound> COMPOUND = new MojangsonValueType<>(MojangsonCompound.class) {
        @Override
        public MojangsonCompound cast(Object value) {
            if (value instanceof MojangsonCompound mojangsonCompound) return mojangsonCompound;
            else if (value instanceof Map<?,?> map) {
                final Map<String, MojangsonValue<?>> compound = new HashMap<>();

                for (final Object k : map.keySet()) {
                    if (k instanceof String strKey) {
                        final Object val = map.get(strKey);
                        compound.put(strKey, get(val).cast(val));
                    }
                    else {
                        throw new IllegalArgumentException("A key of Map is not a string");
                    }
                }

                return new MojangsonCompound(compound);
            }
            else throw new IllegalArgumentException("Map<String, ?>型でない値はMojangsonCompoundに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonList> LIST = new MojangsonValueType<>(MojangsonList.class) {
        @Override
        public MojangsonList cast(Object value) {
            if (value instanceof MojangsonList mojangsonList) return mojangsonList;
            else if (value instanceof List<?> list) {
                final List<MojangsonValue<?>> listOfMojangson = new ArrayList<>();

                for (final Object element : list) {
                    listOfMojangson.add(get(element).cast(element));
                }

                return new MojangsonList(listOfMojangson);
            }
            else throw new IllegalArgumentException("List<?>型でない値はMojangsonListに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonNull> NULL = new MojangsonValueType<>(MojangsonNull.class) {
        @Override
        public MojangsonNull cast(Object value) {
            if (value instanceof MojangsonNull mojangsonNull) return mojangsonNull;
            else if (value == null) return MojangsonNull.NULL;
            else throw new IllegalArgumentException("nullでない値はMojangsonNullに変換できません");
        }
    };

    public static @NotNull MojangsonValueType<?> get(Object value) {
        return switch (value) {
            case Boolean ignored -> BYTE;
            case Byte ignored -> BYTE;
            case Short ignored -> SHORT;
            case Integer ignored -> INT;
            case Long ignored -> LONG;
            case Float ignored -> FLOAT;
            case Double ignored -> DOUBLE;
            case Character ignored -> STRING;
            case String ignored -> STRING;
            case byte[] ignored -> BYTE_ARRAY;
            case int[] ignored -> INT_ARRAY;
            case long[] ignored -> LONG_ARRAY;
            case Map<?, ?> v -> {
                COMPOUND.cast(v);
                yield COMPOUND;
            }
            case List<?> v -> {
                LIST.cast(v);
                yield LIST;
            }
            case MojangsonValue<?> v -> get(v.value);
            case null -> NULL;
            default -> throw new IllegalArgumentException("対応していない型の値(" + value.getClass().getName() + "型)が渡されました");
        };
    }
}
