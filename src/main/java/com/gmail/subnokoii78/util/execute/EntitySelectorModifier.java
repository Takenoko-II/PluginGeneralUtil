package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class EntitySelectorModifier<T extends Entity> {
    private EntitySelectorModifier() {}

    abstract @NotNull List<T> modify(@NotNull List<T> entities, @NotNull SourceStack stack);

    abstract int getPriority();

    public static final Builder<Entity, Vector3Builder> XYZ = new Builder<>(3) {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Vector3Builder argument) {
            stack.write(argument);
            return entities;
        }
    };

    public static final Builder<Entity, EntityType> TYPE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull EntityType argument) {
            return entities.stream()
                .filter(entity -> entity.getType().equals(argument))
                .toList();
        }
    };

    public static final Builder<Entity, String> NAME = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull String argument) {
            return entities.stream()
                .filter(entity -> {
                    final Component customName = entity.customName();
                    if (customName == null) {
                        return argument.isEmpty();
                    }
                    else if (customName instanceof TextComponent textComponent) {
                        return textComponent.content().equals(argument);
                    }
                    else {
                        return false;
                    }
                })
                .toList();
        }
    };

    public static final Builder<Entity, String> TAG = new Builder<>() {
        @Override
        @NotNull
        List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull String argument) {
            return entities.stream()
                .filter(entity -> entity.getScoreboardTags().contains(argument))
                .toList();
        }
    };

    public static final Builder<Entity, PositiveDoubleRange> DISTANCE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull PositiveDoubleRange argument) {
            return entities.stream()
                .filter(entity -> {
                    final double distance = stack.getLocation().getDistanceTo(Vector3Builder.from(entity));
                    return argument.min() <= distance && distance <= argument.max();
                })
                .toList();
        }
    };

    public static final Builder<Entity, SelectorSortOrder> SORT = new Builder<>(2) {
        @NotNull
        @Override
        List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull SelectorSortOrder argument) {
            return argument.sort(entities, stack);
        }
    };

    public static final Builder<Entity, Vector3Builder> D_XYZ = new Builder<>() {
        @NotNull
        @Override
        List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Vector3Builder argument) {
            final BoundingBox box = BoundingBox.of(stack.getLocation().toBukkitVector(), argument.toBukkitVector());
            return entities.stream()
                .filter(entity -> entity.getBoundingBox().overlaps(box))
                .toList();
        }
    };

    public static final Builder<Player, GameMode> GAME_MODE = new Builder<>() {
        @Override
        @NotNull List<Player> modify(@NotNull List<Player> entities, @NotNull SourceStack stack, @NotNull GameMode argument) {
            return entities.stream()
                .filter(player -> player.getGameMode().equals(argument))
                .toList();
        }
    };

    public static final Builder<Player, PositiveIntRange> LEVEL = new Builder<>() {
        @NotNull
        @Override
        List<Player> modify(@NotNull List<Player> entities, @NotNull SourceStack stack, @NotNull PositiveIntRange argument) {
            return entities.stream()
                .filter(player -> argument.min() <= player.getLevel() && player.getLevel() <= argument.max())
                .toList();
        }
    };

    public static final Builder<Entity, PositiveFloatRange> X_ROTATION = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull PositiveFloatRange argument) {
            return entities.stream()
                .filter(entity -> argument.min() <= entity.getLocation().getPitch() && entity.getLocation().getPitch() <= argument.max())
                .toList();
        }
    };

    public static final Builder<Entity, PositiveFloatRange> Y_ROTATION = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull PositiveFloatRange argument) {
            return entities.stream()
                .filter(entity -> argument.min() <= entity.getLocation().getYaw() && entity.getLocation().getYaw() <= argument.max())
                .toList();
        }
    };

    public static final Builder<Entity, Team> TEAM = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Team argument) {
            return entities.stream()
                .filter(argument::hasEntity)
                .toList();
        }
    };

    public static final Builder<Player, Map<Advancement, Boolean>> ADVANCEMENTS = new Builder<>() {
        @Override
        @NotNull
        List<Player> modify(@NotNull List<Player> entities, @NotNull SourceStack stack, @NotNull Map<Advancement, Boolean> argument) {
            return entities.stream()
                .filter(player -> {
                    for (final Advancement advancement : argument.keySet()) {
                        final boolean flag = argument.get(advancement);
                        if (player.getAdvancementProgress(advancement).isDone() != flag) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
        }
    };

    public static final Builder<Entity, Map<String, IntRange>> SCORES = new Builder<>() {
        @Override
        @NotNull
        List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Map<String, IntRange> argument) {
            return entities.stream()
                .filter(entity -> {
                    for (final String name : argument.keySet()) {
                        final ScoreboardUtils.Objective objective = ScoreboardUtils.getObjective(name);
                        final IntRange range = argument.get(name);

                        if (objective == null) return false;

                        if (range.min() <= objective.getScore(entity) && objective.getScore(entity) <= range.max()) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
        }
    };

    public static final Builder<Entity, Integer> LIMIT = new Builder<>(1) {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Integer argument) {
            return entities.subList(0, argument);
        }
    };

    public static abstract class Builder<T extends Entity, U> {
        private final int priority;

        private Builder(int priority) {
            this.priority = priority;
        }

        private Builder() {
            this.priority = 0;
        }

        abstract @NotNull List<T> modify(@NotNull List<T> entities, @NotNull SourceStack stack, @NotNull U argument);

        @NotNull EntitySelectorModifier<T> build(@NotNull U value) {
            final Builder<T, U> that = this;

            return new EntitySelectorModifier<>() {
                @Override
                @NotNull List<T> modify(@NotNull List<T> entities, @NotNull SourceStack stack) {
                    return that.modify(entities, stack, value);
                }

                @Override
                int getPriority() {
                    return priority;
                }
            };
        }
    }
}
