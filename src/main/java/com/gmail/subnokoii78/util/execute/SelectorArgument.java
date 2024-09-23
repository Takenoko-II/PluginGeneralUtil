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
import java.util.function.BiPredicate;

/**
 * セレクター引数を表現するクラス
 */
public abstract class SelectorArgument {
    private SelectorArgument() {}

    abstract @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack);

    abstract int getPriority();

    abstract @NotNull String getId();

    static final Builder<SelectorArgument> NOT = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull SelectorArgument argument) {
            final List<Entity> selected = argument.modify(entities, stack);
            return entities.stream()
                .filter(entity -> !selected.contains(entity))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "!";
        }
    };

    /**
     * セレクター引数x=
     */
    public static final Builder<Double> X = new Builder<>(3) {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Double argument) {
            stack.write(stack.getPosition().x(argument));
            return entities;
        }

        @Override
        public @NotNull String getId() {
            return "x";
        }
    };

    /**
     * セレクター引数y=
     */
    public static final Builder<Double> Y = new Builder<>(3) {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Double argument) {
            stack.write(stack.getPosition().y(argument));
            return entities;
        }

        @Override
        public @NotNull String getId() {
            return "y";
        }
    };

    /**
     * セレクター引数z=
     */
    public static final Builder<Double> Z = new Builder<>(3) {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Double argument) {
            stack.write(stack.getPosition().z(argument));
            return entities;
        }

        @Override
        public @NotNull String getId() {
            return "z";
        }
    };

    /**
     * セレクター引数type=
     */
    public static final Builder<EntityType> TYPE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull EntityType argument) {
            return entities.stream()
                .filter(entity -> entity.getType().equals(argument))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "type";
        }
    };

    /**
     * セレクター引数name=
     */
    public static final Builder<String> NAME = new Builder<>() {
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

        @Override
        public @NotNull String getId() {
            return "name";
        }
    };

    /**
     * セレクター引数tag=
     */
    public static final Builder<String> TAG = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull String argument) {
            return entities.stream()
                .filter(entity -> entity.getScoreboardTags().contains(argument))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "tag";
        }
    };

    /**
     * セレクター引数distance=
     */
    public static final Builder<DistanceRange> DISTANCE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull DistanceRange argument) {
            return entities.stream()
                .filter(entity -> {
                    final double distance = stack.getPosition().getDistanceTo(Vector3Builder.from(entity));
                    return argument.min() <= distance && distance <= argument.max();
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "distance";
        }
    };

    /**
     * セレクター引数sort=
     */
    public static final Builder<SelectorSortOrder> SORT = new Builder<>(2) {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull SelectorSortOrder argument) {
            return argument.sort(entities, stack);
        }

        @Override
        public @NotNull String getId() {
            return "sort";
        }
    };

    /**
     * セレクター引数dx=, dy=, dz=を一つにしたもの(各軸の範囲の最小値は1ではなく0)
     */
    public static final Builder<Vector3Builder> DXYZ = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Vector3Builder argument) {
            final BoundingBox box = BoundingBox.of(
                stack.getPosition().toBukkitVector(),
                stack.getPosition()
                    .add(argument)
                    .toBukkitVector()
            );

            return entities.stream()
                .filter(entity -> entity.getBoundingBox().overlaps(box))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "dxyz";
        }
    };

    /**
     * セレクター引数gamemode=
     */
    public static final Builder<GameMode> GAMEMODE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull GameMode argument) {
            return entities.stream()
                .filter(entity -> {
                    if (entity instanceof Player player) {
                        return player.getGameMode().equals(argument);
                    }
                    else return false;
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "gamemode";
        }
    };

    /**
     * セレクター引数level=
     */
    public static final Builder<LevelRange> LEVEL = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull LevelRange argument) {
            return entities.stream()
                .filter(entity -> {
                    if (entity instanceof Player player) {
                        return argument.min() <= player.getLevel() && player.getLevel() <= argument.max();
                    }
                    else return false;
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "level";
        }
    };

    /**
     * セレクター引数x_rotation=
     */
    public static final Builder<RotationRange> X_ROTATION = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull RotationRange argument) {
            return entities.stream()
                .filter(entity -> argument.min() <= entity.getLocation().getPitch() && entity.getLocation().getPitch() <= argument.max())
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "x_rotation";
        }
    };

    /**
     * セレクター引数y_rotation=
     */
    public static final Builder<RotationRange> Y_ROTATION = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull RotationRange argument) {
            return entities.stream()
                .filter(entity -> argument.min() <= entity.getLocation().getYaw() && entity.getLocation().getYaw() <= argument.max())
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "y_rotation";
        }
    };

    /**
     * セレクター引数team=
     */
    public static final Builder<Team> TEAM = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Team argument) {
            return entities.stream()
                .filter(argument::hasEntity)
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "team";
        }
    };

    /**
     * セレクター引数advancements=
     */
    public static final Builder<Map<Advancement, Boolean>> ADVANCEMENTS = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Map<Advancement, Boolean> argument) {
            return entities.stream()
                .filter(entity -> {
                    if (entity instanceof Player player) {
                        for (final Advancement advancement : argument.keySet()) {
                            final boolean flag = argument.get(advancement);
                            if (player.getAdvancementProgress(advancement).isDone() != flag) {
                                return false;
                            }
                        }

                        return true;
                    }
                    else return false;
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "advancements";
        }
    };

    /**
     * セレクター引数scores=
     */
    public static final Builder<Map<String, ScoreRange>> SCORES = new Builder<>() {
        @Override
        @NotNull
        List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Map<String, ScoreRange> argument) {
            return entities.stream()
                .filter(entity -> {
                    for (final String name : argument.keySet()) {
                        final ScoreboardUtils.Objective objective = ScoreboardUtils.getObjective(name);
                        final ScoreRange range = argument.get(name);

                        if (objective == null) return false;

                        if (range.min() <= objective.getScore(entity) && objective.getScore(entity) <= range.max()) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "scores";
        }
    };

    /**
     * セレクター引数limit=
     */
    public static final Builder<Integer> LIMIT = new Builder<>(1) {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Integer argument) {
            return entities.subList(0, argument);
        }

        @Override
        public @NotNull String getId() {
            return "limit";
        }
    };

    /**
     * 任意の条件に一致することを条件とするセレクター引数
     */
    public static final Builder<BiPredicate<Entity, SourceStack>> PREDICATE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull BiPredicate<Entity, SourceStack> argument) {
            return entities.stream()
                .filter(entity -> argument.test(entity, stack.copy()))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "predicate";
        }
    };

    /**
     * 新しくセレクター引数を生成するためのクラス
     * @param <U> セレクター引数に渡される値の型
     */
    public static abstract class Builder<U> {
        private final int priority;

        private Builder(int priority) {
            this.priority = priority;
        }

        private Builder() {
            this.priority = 0;
        }

        abstract @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull U argument);

        /**
         * セレクター引数のIDを取得します。
         * @return ID
         */
        public abstract @NotNull String getId();

        @NotNull
        SelectorArgument build(@NotNull U value) {
            final Builder<U> that = this;

            return new SelectorArgument() {
                @Override
                @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
                    return that.modify(entities, stack, value);
                }

                @Override
                int getPriority() {
                    return priority;
                }

                @Override
                public @NotNull String getId() {
                    return that.getId();
                }
            };
        }
    }
}
