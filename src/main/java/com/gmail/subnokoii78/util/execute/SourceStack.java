package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.file.json.JSONArray;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class SourceStack {
    private Entity executor = null;

    private World dimension = Bukkit.getWorlds().getFirst();

    private final Vector3Builder location = new Vector3Builder();

    private final DualAxisRotationBuilder rotation = new DualAxisRotationBuilder();

    private EntityAnchor anchor = EntityAnchor.FEET;

    public SourceStack() {}

    public SourceStack(@NotNull Entity source) {
        write(source);
        write(source.getWorld());
        write(Vector3Builder.from(source));
        write(DualAxisRotationBuilder.from(source));
    }

    public SourceStack(@NotNull Block source) {
        write(source.getWorld());
        write(Vector3Builder.from(source.getLocation()));
        write(DualAxisRotationBuilder.from(source.getLocation()));
    }

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

    public @NotNull String getEntityAnchorId() {
        return anchor.getId();
    }

    public @NotNull Vector3Builder getEntityAnchorOffset() {
        return anchor.getOffset(executor);
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

    void write(@NotNull EntityAnchor anchor) {
        this.anchor = anchor;
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
        final Vector3Builder out = location.copy();

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

        return out.add(getEntityAnchorOffset());
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

    public @NotNull Set<Character> readAxes(@NotNull String axes) {
        final Set<String> chars = Set.of(axes.split(""));

        if (axes.length() > 3) throw new IllegalArgumentException("軸は3つまで指定可能です");
        else if (axes.length() != chars.size()) throw new IllegalArgumentException("軸が重複しています");
        else if (!Set.of("x", "y", "z").containsAll(chars)) {
            throw new IllegalArgumentException("x, y, zの文字が有効です");
        }

        return chars.stream()
            .map(c -> c.charAt(0))
            .collect(Collectors.toSet());
    }

    public <T extends Entity> @NotNull List<T> getEntities(@NotNull EntitySelector<T> selector) {
        return selector.getEntities(this);
    }

    public @NotNull SourceStack copy() {
        final SourceStack stack = new SourceStack();
        stack.write(dimension);
        stack.write(executor);
        stack.write(location.copy());
        stack.write(rotation.copy());
        stack.write(anchor);
        return stack;
    }

    public @NotNull JSONObject getAsJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.set("executor", executor == null ? "null" : executor.getName());
        final JSONArray loc = new JSONArray();
        loc.add(location.x());
        loc.add(location.y());
        loc.add(location.z());
        jsonObject.set("location", loc);
        final JSONArray rot = new JSONArray();
        rot.add(rotation.yaw());
        rot.add(rotation.pitch());
        jsonObject.set("rotation", rot);
        jsonObject.set("anchor", anchor.getId());
        jsonObject.set("dimension", DimensionProvider.get(dimension).getId());

        return jsonObject;
    }

    public boolean runCommand(@NotNull String command) {
        final String common = String.format(
            "in %s positioned %s rotated %s",
            DimensionProvider.get(dimension).getId(),
            location.format("$c $c $c"),
            rotation.format("$c $c")
        );

        try {
            if (executor == null) {
                Bukkit.getServer().dispatchCommand(
                    NULL_EXECUTOR,
                    String.format(
                        "execute %s run %s",
                        common,
                        command
                    )
                );
            }
            else {
                Bukkit.getServer().dispatchCommand(
                    executor,
                    String.format(
                        "execute %s as %s anchored %s run %s",
                        common,
                        executor.getUniqueId(),
                        anchor.getId(),
                        command
                    )
                );
            }
        }
        catch (CommandException e) {
            return false;
        }

        return true;
    }

    private static final CommandSender NULL_EXECUTOR = Bukkit.createCommandSender(component -> {});
}
