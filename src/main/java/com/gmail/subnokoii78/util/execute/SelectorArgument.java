package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.scoreboard.ScoreObjective;
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

/**
 * セレクター引数を表現するクラス
 */
public abstract class SelectorArgument {
    protected final Object value;

    private SelectorArgument(Object value) {
        this.value = value;
    }

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

        @Override
        public @NotNull Class<SelectorArgument> getArgumentType() {
            return SelectorArgument.class;
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

        @Override
        public @NotNull Class<Double> getArgumentType() {
            return Double.class;
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

        @Override
        public @NotNull Class<Double> getArgumentType() {
            return Double.class;
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

        @Override
        public @NotNull Class<Double> getArgumentType() {
            return Double.class;
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

        @Override
        public @NotNull Class<EntityType> getArgumentType() {
            return EntityType.class;
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

        @Override
        public @NotNull Class<String> getArgumentType() {
            return String.class;
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

        @Override
        public @NotNull Class<String> getArgumentType() {
            return String.class;
        }
    };

    /**
     * セレクター引数distance=
     */
    public static final Builder<NumberRange.DistanceRange> DISTANCE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull NumberRange.DistanceRange argument) {
            return entities.stream()
                .filter(entity -> {
                    if (!stack.getDimension().equals(entity.getWorld())) return false;
                    final double distance = stack.getPosition().getDistanceTo(Vector3Builder.from(entity));
                    return argument.within(distance);
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "distance";
        }

        @Override
        public @NotNull Class<NumberRange.DistanceRange> getArgumentType() {
            return NumberRange.DistanceRange.class;
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

        @Override
        public @NotNull Class<SelectorSortOrder> getArgumentType() {
            return SelectorSortOrder.class;
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
                .filter(entity -> {
                    if (!stack.getDimension().equals(entity.getWorld())) return false;
                    return entity.getBoundingBox().overlaps(box);
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "dxyz";
        }

        @Override
        public @NotNull Class<Vector3Builder> getArgumentType() {
            return Vector3Builder.class;
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

        @Override
        public @NotNull Class<GameMode> getArgumentType() {
            return GameMode.class;
        }
    };

    /**
     * セレクター引数level=
     */
    public static final Builder<NumberRange.LevelRange> LEVEL = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull NumberRange.LevelRange argument) {
            return entities.stream()
                .filter(entity -> {
                    if (entity instanceof Player player) {
                        return argument.within(player.getLevel());
                    }
                    else return false;
                })
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "level";
        }

        @Override
        public @NotNull Class<NumberRange.LevelRange> getArgumentType() {
            return NumberRange.LevelRange.class;
        }
    };

    /**
     * セレクター引数x_rotation=
     */
    public static final Builder<NumberRange.RotationRange> X_ROTATION = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull NumberRange.RotationRange argument) {
            return entities.stream()
                .filter(entity -> argument.within(entity.getLocation().getPitch()))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "x_rotation";
        }

        @Override
        public @NotNull Class<NumberRange.RotationRange> getArgumentType() {
            return NumberRange.RotationRange.class;
        }
    };

    /**
     * セレクター引数y_rotation=
     */
    public static final Builder<NumberRange.RotationRange> Y_ROTATION = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull NumberRange.RotationRange argument) {
            return entities.stream()
                .filter(entity -> argument.within(entity.getLocation().getYaw()))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "y_rotation";
        }

        @Override
        public @NotNull Class<NumberRange.RotationRange> getArgumentType() {
            return NumberRange.RotationRange.class;
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

        @Override
        public @NotNull Class<Team> getArgumentType() {
            return Team.class;
        }
    };

    /**
     * セレクター引数advancements=
     */
    public static final Builder<Advancements> ADVANCEMENTS = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Advancements argument) {
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

        @Override
        public @NotNull Class<Advancements> getArgumentType() {
            return Advancements.class;
        }
    };

    /**
     * セレクター引数scores=
     */
    public static final Builder<Scores> SCORES = new Builder<>() {
        @Override
        @NotNull
        List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull Scores argument) {
            return entities.stream()
                .filter(entity -> {
                    for (final String name : argument.keySet()) {
                        if (!ScoreboardUtils.isRegistered(name)) return false;

                        final ScoreObjective objective = ScoreboardUtils.getObjective(name);
                        final NumberRange<Integer> range = argument.get(name);

                        if (range.within(objective.getScore(entity))) {
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

        @Override
        public @NotNull Class<Scores> getArgumentType() {
            return Scores.class;
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

        @Override
        public @NotNull Class<Integer> getArgumentType() {
            return Integer.class;
        }
    };

    /**
     * 任意の条件に一致することを条件とするセレクター引数
     */
    public static final Builder<EntityPredicate> PREDICATE = new Builder<>() {
        @Override
        @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull EntityPredicate argument) {
            return entities.stream()
                .filter(entity -> argument.test(entity, stack.copy()))
                .toList();
        }

        @Override
        public @NotNull String getId() {
            return "predicate";
        }

        @Override
        public @NotNull Class<EntityPredicate> getArgumentType() {
            return EntityPredicate.class;
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
            this(0);
        }

        abstract @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull U argument);

        /**
         * セレクター引数のIDを取得します。
         * @return ID
         */
        public abstract @NotNull String getId();

        public abstract @NotNull Class<U> getArgumentType();

        @NotNull
        SelectorArgument build(@NotNull U parameter) {
            final Builder<U> that = this;

            return new SelectorArgument(parameter) {
                @Override
                @NotNull List<Entity> modify(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
                    return that.modify(entities, stack, parameter);
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
