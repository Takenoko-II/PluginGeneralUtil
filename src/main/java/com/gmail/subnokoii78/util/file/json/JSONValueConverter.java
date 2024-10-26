package com.gmail.subnokoii78.util.file.json;

import com.gmail.subnokoii78.util.execute.VanillaDimensionProvider;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JSONValueConverter<T> {
    private final Class<T> clazz;

    protected JSONValueConverter(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract @Nullable T tryConvert(@NotNull Object value);

    public boolean isConvertable(@NotNull Object value) {
        return tryConvert(value) != null;
    }

    protected @NotNull T convert(@NotNull Object value) {
        final var v = tryConvert(value);
        if (v == null) throw new IllegalArgumentException("渡された値は " + clazz.getName() + " に変換できません");
        else return v;
    }

    public static final JSONValueConverter<Vector3Builder> VECTOR3 = new JSONValueConverter<>(Vector3Builder.class) {
        private boolean isNumberKey(@NotNull JSONObject jsonObject, @NotNull String key) {
            if (jsonObject.hasKey(key)) {
                return jsonObject.getTypeOfKey(key).equals(JSONValueType.NUMBER);
            }
            else return false;
        }

        private @Nullable Vector3Builder object(@NotNull JSONObject jsonObject) {
            if (isNumberKey(jsonObject, "x") && isNumberKey(jsonObject, "y") && isNumberKey(jsonObject, "z")) {
                return new Vector3Builder(
                    jsonObject.getKey("x", JSONValueType.NUMBER).doubleValue(),
                    jsonObject.getKey("y", JSONValueType.NUMBER).doubleValue(),
                    jsonObject.getKey("z", JSONValueType.NUMBER).doubleValue()
                );
            }
            else return null;
        }

        private @Nullable Vector3Builder array(@NotNull JSONArray jsonArray) {
            if (jsonArray.length() == 3 && jsonArray.isArrayOf(JSONValueType.NUMBER)) {
                final TypedJSONArray<Number> typedJSONArray = jsonArray.typed(JSONValueType.NUMBER);
                return new Vector3Builder(
                    typedJSONArray.get(0).doubleValue(),
                    typedJSONArray.get(1).doubleValue(),
                    typedJSONArray.get(2).doubleValue()
                );
            }
            else return null;
        }

        @Override
        protected @Nullable Vector3Builder tryConvert(@NotNull Object value) {
            return switch (value) {
                case JSONObject jsonObject -> object(jsonObject);
                case JSONArray jsonArray -> array(jsonArray);
                default -> null;
            };
        }
    };

    public static final JSONValueConverter<DualAxisRotationBuilder> DUAL_AXIS_ROTATION = new JSONValueConverter<>(DualAxisRotationBuilder.class) {
        private boolean isNumberKey(@NotNull JSONObject jsonObject, @NotNull String key) {
            if (jsonObject.hasKey(key)) {
                return jsonObject.getTypeOfKey(key).equals(JSONValueType.NUMBER);
            }
            else return false;
        }

        private @Nullable DualAxisRotationBuilder object(@NotNull JSONObject jsonObject) {
            if (isNumberKey(jsonObject, "yaw") && isNumberKey(jsonObject, "pitch")) {
                return new DualAxisRotationBuilder(
                    jsonObject.getKey("yaw", JSONValueType.NUMBER).floatValue(),
                    jsonObject.getKey("pitch", JSONValueType.NUMBER).floatValue()
                );
            }
            else return null;
        }

        private @Nullable DualAxisRotationBuilder array(@NotNull JSONArray jsonArray) {
            if (jsonArray.length() == 2 && jsonArray.isArrayOf(JSONValueType.NUMBER)) {
                final TypedJSONArray<Number> typedJSONArray = jsonArray.typed(JSONValueType.NUMBER);
                return new DualAxisRotationBuilder(
                    typedJSONArray.get(0).floatValue(),
                    typedJSONArray.get(1).floatValue()
                );
            }
            else return null;
        }

        @Override
        protected @Nullable DualAxisRotationBuilder tryConvert(@NotNull Object value) {
            return switch (value) {
                case JSONObject jsonObject -> object(jsonObject);
                case JSONArray jsonArray -> array(jsonArray);
                default -> null;
            };
        }
    };

    public static final JSONValueConverter<TripleAxisRotationBuilder> TRIPLE_AXIS_ROTATION = new JSONValueConverter<>(TripleAxisRotationBuilder.class) {
        private boolean isNumberKey(@NotNull JSONObject jsonObject, @NotNull String key) {
            if (jsonObject.hasKey(key)) {
                return jsonObject.getTypeOfKey(key).equals(JSONValueType.NUMBER);
            }
            else return false;
        }

        private @Nullable TripleAxisRotationBuilder object(@NotNull JSONObject jsonObject) {
            if (isNumberKey(jsonObject, "yaw") && isNumberKey(jsonObject, "pitch") && isNumberKey(jsonObject, "roll")) {
                return new TripleAxisRotationBuilder(
                    jsonObject.getKey("yaw", JSONValueType.NUMBER).floatValue(),
                    jsonObject.getKey("pitch", JSONValueType.NUMBER).floatValue(),
                    jsonObject.getKey("roll", JSONValueType.NUMBER).floatValue()
                );
            }
            else return null;
        }

        private @Nullable TripleAxisRotationBuilder array(@NotNull JSONArray jsonArray) {
            if (jsonArray.length() == 3 && jsonArray.isArrayOf(JSONValueType.NUMBER)) {
                final TypedJSONArray<Number> typedJSONArray = jsonArray.typed(JSONValueType.NUMBER);
                return new TripleAxisRotationBuilder(
                    typedJSONArray.get(0).floatValue(),
                    typedJSONArray.get(1).floatValue(),
                    typedJSONArray.get(2).floatValue()
                );
            }
            else return null;
        }

        @Override
        protected @Nullable TripleAxisRotationBuilder tryConvert(@NotNull Object value) {
            return switch (value) {
                case JSONObject jsonObject -> object(jsonObject);
                case JSONArray jsonArray -> array(jsonArray);
                default -> null;
            };
        }
    };

    public static final JSONValueConverter<Location> LOCATION_BUKKIT = new JSONValueConverter<>(Location.class) {
        @Override
        protected @Nullable Location tryConvert(@NotNull Object value) {
            if (!(value instanceof JSONObject jsonObject)) return null;
            else if (!(jsonObject.hasKey("dimension") && jsonObject.hasKey("location") && jsonObject.hasKey("rotation"))) return null;
            else if (!(jsonObject.getTypeOfKey("dimension").equals(JSONValueType.STRING))) return null;

            final World dimension = VanillaDimensionProvider.get(jsonObject.getKey("dimension", JSONValueType.STRING)).getWorld();
            final Object location = jsonObject.getKey("location", jsonObject.getTypeOfKey("location"));
            final Object rotation = jsonObject.getKey("rotation", jsonObject.getTypeOfKey("rotation"));

            if (VECTOR3.isConvertable(location) && DUAL_AXIS_ROTATION.isConvertable(rotation)) {
                final Vector3Builder vec3 = VECTOR3.convert(location);
                final DualAxisRotationBuilder rot = DUAL_AXIS_ROTATION.convert(rotation);
                return new Location(
                    dimension,
                    vec3.x(), vec3.y(), vec3.z(),
                    rot.yaw(), rot.pitch()
                );
            }
            else return null;
        }
    };
}
