package com.gmail.subnokoii78.util.vector.execute;

import com.gmail.subnokoii78.util.function.TiFunction;
import com.gmail.subnokoii78.util.other.TupleT;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public class EntitySelectorModifier<T extends Entity> {
    private final BiFunction<List<T>, SourceStack, List<T>> function;

    private EntitySelectorModifier(@NotNull BiFunction<List<T>, SourceStack, List<T>> function) {
        this.function = function;
    }

    @NotNull List<T> modify(@NotNull List<T> entities, @NotNull SourceStack stack) {
        return function.apply(entities, stack);
    }

    public static final Builder<Entity, EntityType> TYPE = new Builder<Entity, EntityType>()
        .function((entities, stack, value) -> {
            return entities.stream()
                .filter(entity -> entity.getType().equals(value))
                .toList();
        });

    public static final Builder<Entity, String> NAME = new Builder<Entity, String>()
        .function((entities, stack, value) -> {
            return entities.stream()
                .filter(entity -> {
                    final Component customName = entity.customName();
                    if (customName == null) {
                        return value.isEmpty();
                    }
                    else if (customName instanceof TextComponent textComponent) {
                        return textComponent.content().equals(value);
                    }
                    else {
                        return false;
                    }
                })
                .toList();
        });

    public static final Builder<Entity, String> TAG = new Builder<Entity, String>()
        .function((entities, stack, value) -> {
            return entities.stream()
                .filter(entity -> entity.getScoreboardTags().contains(value))
                .toList();
        });

    public static final Builder<Entity, TupleT<Double>> DISTANCE = new Builder<Entity, TupleT<Double>>()
        .function((entities, stack, range) -> {
            return entities.stream()
                .filter(entity -> {
                    final double distance = stack.getLocation().getDistanceTo(Vector3Builder.from(entity));
                    return range.left() <= distance && distance <= range.right();
                })
                .toList();
        });

    public static final class Builder<T extends Entity, U> {
        private TiFunction<List<T>, SourceStack, U, List<T>> function;

        private Builder() {}

        public Builder<T, U> function(@NotNull TiFunction<List<T>, SourceStack, U, List<T>> function) {
            this.function = function;
            return this;
        }

        @NotNull EntitySelectorModifier<T> build(@NotNull U value) {
            return new EntitySelectorModifier<>((entities, stack) -> function.apply(entities, stack, value));
        }
    }
}
