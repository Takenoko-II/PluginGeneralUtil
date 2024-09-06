package com.gmail.subnokoii78.util.vector.execute;

import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SourceStack {
    private Entity executor = null;

    private World dimension = Bukkit.getWorlds().getFirst();

    private final Vector3Builder location = new Vector3Builder();

    private final DualAxisRotationBuilder rotation = new DualAxisRotationBuilder();

    private final Vector3Builder entityAnchor = new Vector3Builder();

    SourceStack() {}

    public @Nullable Entity getExecutor() {
        return executor;
    }

    public @NotNull World getDimension() {
        return dimension;
    }

    public @NotNull Vector3Builder getLocation() {
        return location.copy();
    }

    public @NotNull DualAxisRotationBuilder getRotation() {
        return rotation.copy();
    }

    public @NotNull Vector3Builder getEntityAnchor() {
        return entityAnchor;
    }

    void write(@NotNull Entity executor) {
        this.executor = executor;
    }

    void write(@NotNull World dimension) {
        this.dimension = dimension;
    }

    void write(@NotNull Vector3Builder location) {
        this.location.x(location.x()).y(location.y()).z(location.z());
    }

    void write(@NotNull DualAxisRotationBuilder rotation) {
        this.rotation.yaw(rotation.yaw()).pitch(rotation.pitch());
    }

    void anchored(@NotNull EntityAnchor anchor) {
        this.entityAnchor.add(anchor.getEntityAnchor(executor));
    }

    private double parseAbsolutePos(@NotNull String input) {
        if (input.matches("^[+-]?\\d$")) {
            return Double.parseDouble(input) + 0.5d;
        }
        else if (input.matches("^[+-]?\\d+(?:\\.\\d+)?$")) {
            return Double.parseDouble(input);
        }
        else {
            throw new IllegalArgumentException("絶対座標が期待されています");
        }
    }

    private double parseRelativePos(@NotNull String input, int axis) {
        if (input.matches("^~(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
            final String number = input.substring(1);
            final double offset = number.isEmpty() ? 0 : Double.parseDouble(number);

            return switch (axis) {
                case 0 -> offset + location.x();
                case 1 -> offset + location.y();
                case 2 -> offset + location.z();
                default ->
                    throw new IllegalArgumentException("NEVER HAPPENS");
            };
        }
        else {
            throw new IllegalArgumentException("相対座標が期待されています");
        }
    }

    private @NotNull Vector3Builder parseLocalPos(@NotNull List<String> components) {
        final Vector3Builder out = new Vector3Builder();

        int i = 0;
        for (final String input : components) {
            if (input.matches("^\\^(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
                final Vector3Builder.LocalAxisProvider localAxis = rotation.getDirection3d().getLocalAxisProvider();
                final String number = input.substring(1);
                final double length = number.isEmpty() ? 0 : Double.parseDouble(number);

                final Vector3Builder offset = switch (i) {
                    case 0 -> localAxis.getX().length(length);
                    case 1 -> localAxis.getY().length(length);
                    case 2 -> localAxis.getZ().length(length);
                    default ->
                        throw new IllegalArgumentException("NEVER HAPPENS");
                };

                out.add(offset);
            }
            else {
                throw new IllegalArgumentException("ローカル座標が期待されています");
            }

            i++;
        }

        return out.add(getEntityAnchor());
    }

    public @NotNull Vector3Builder readCoordinates(@NotNull String coordinates) {
        final List<String> componentInputs = List.of(coordinates.split("\\s"));
        final List<Double> componentOutputs = new ArrayList<>();

        if (componentInputs.size() != 3) throw new IllegalArgumentException("座標は三軸です");

        for (int i = 0; i < componentInputs.size(); i++) {
            final String value = componentInputs.get(i);

            if (value.startsWith("~")) {
                componentOutputs.add(parseRelativePos(value, i));
            }
            else if (value.startsWith("^")) {
                if (i == 0) {
                    final Vector3Builder v = parseLocalPos(componentInputs);
                    componentOutputs.addAll(List.of(v.x(), v.y(), v.z()));
                    break;
                }
                else throw new IllegalArgumentException("ローカル座標とほかの記述形式を混ぜることはできません");
            }
            else {
                componentOutputs.add(parseAbsolutePos(value));
            }
        }

        return new Vector3Builder(componentOutputs.get(0), componentOutputs.get(1), componentOutputs.get(2));
    }

    private float parseAbsoluteRot(@NotNull String input) {
        if (input.matches("^[+-]?\\d+(?:\\.\\d+)?$")) {
            return Float.parseFloat(input);
        }
        else {
            throw new IllegalArgumentException("絶対角度が期待されています");
        }
    }

    private float parseRelativeRot(@NotNull String input, int axis) {
        if (input.matches("^~(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
            final String number = input.substring(1);
            final float offset = number.isEmpty() ? 0 : Float.parseFloat(number);

            return switch (axis) {
                case 0 -> offset + rotation.yaw();
                case 1 -> offset + rotation.pitch();
                default ->
                    throw new IllegalArgumentException("NEVER HAPPENS");
            };
        }
        else {
            throw new IllegalArgumentException("相対座標が期待されています");
        }
    }

    public @NotNull DualAxisRotationBuilder readAngles(@NotNull String angles) {
        final List<String> componentInputs = List.of(angles.split("\\s"));
        final List<Float> componentOutputs = new ArrayList<>();

        if (componentInputs.size() != 2) throw new IllegalArgumentException("座標は二軸です");

        for (int i = 0; i < componentInputs.size(); i++) {
            final String value = componentInputs.get(i);

            if (value.startsWith("~")) {
                componentOutputs.add(parseRelativeRot(value, i));
            }
            else {
                componentOutputs.add(parseAbsoluteRot(value));
            }
        }

        return new DualAxisRotationBuilder(componentOutputs.get(0), componentOutputs.get(1));
    }

    public <T extends Entity> @NotNull List<T> getEntities(@NotNull EntitySelector<T> selector) {
        return selector.getEntities(this);
    }

    public @NotNull SourceStack copy() {
        final SourceStack stack = new SourceStack();
        stack.write(dimension);
        stack.write(executor);
        stack.write(location);
        stack.write(rotation);
        stack.write(entityAnchor);
        return stack;
    }
}
