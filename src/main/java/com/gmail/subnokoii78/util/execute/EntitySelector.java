package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * エンティティセレクターを表現するクラス
 * @param <T> @pや@aは{@link Player}、@eや@nは{@link Entity}、@sは暫定で{@link Entity}
 */
public final class EntitySelector<T extends Entity> {
    private final List<SelectorArgument> arguments = new ArrayList<>();

    private final Builder<T> builder;

    private EntitySelector(@NotNull Builder<T> builder) {
        this.builder = builder;
    }

    /**
     * セレクターが単一のエンティティを示すことが保障されていることをテストします。
     * @return @p, @n, @e[limit=1]などはtrue
     */
    public boolean isSingle() {
        for (final SelectorArgument argument : arguments) {
            if (argument.getId().equals(SelectorArgument.LIMIT.getId())) {
                return argument.value.equals(1);
            }
        }

        // limitを持っていないとき↓

        return builder.equals(S) || builder.equals(P) || builder.equals(N);
    }

    void addArgument(@NotNull SelectorArgument argument) {
        arguments.add(argument);
        arguments.sort((a, b) -> b.getPriority() - a.getPriority());
    }

    /**
     * セレクターに引数を追加します。
     * @param modifier セレクター引数の種類
     * @param value セレクター引数に渡す値
     * @return thisをそのまま返す
     */
    public <U> @NotNull EntitySelector<T> arg(@NotNull SelectorArgument.Builder<U> modifier, @NotNull U value) {
        if (arguments.stream().anyMatch(argument -> {
            if (argument.getId().equals(SelectorArgument.NOT.getId())) return false;
            else return argument.getId().equals(modifier.getId());
        })) {
            throw new IllegalArgumentException("既に指定された引数を使用することはできません");
        }

        addArgument(modifier.build(value));
        return this;
    }

    /**
     * セレクターに負の引数を追加します。
     * @param modifier セレクター引数の種類
     * @param value セレクター引数に渡す値
     * @return thisをそのまま返す
     */
    public <U> @NotNull EntitySelector<T> notArg(@NotNull SelectorArgument.Builder<U> modifier, @NotNull U value) {
        addArgument(SelectorArgument.NOT.build(modifier.build(value)));
        return this;
    }

    private @NotNull List<T> modifier(@NotNull List<T> entities, @NotNull SourceStack stack) {
        final SourceStack copy = stack.copy();
        List<Entity> out = entities.stream().map(entity -> (Entity) entity).toList();

        // xyz -> sort -> limit の順番
        for (final SelectorArgument modifier : arguments) {
            out = modifier.modify(out, copy);
        }

        // out(List<Entity>)はentities(List<T extends Entity>)をフィルターしたりソートしたものなのでここのキャストでエラーが起こることはない！！！！
        return (List<T>) out;
    }

    @NotNull List<T> getEntities(@NotNull SourceStack stack) {
        return builder.selectorSpecificModifier(modifier(builder.getTargetCandidates(stack), stack), stack, arguments);
    }

    private static @NotNull List<Entity> getAllEntities() {
        return Bukkit.getServer().getWorlds().stream()
            .flatMap(world -> world.getEntities().stream())
            .toList();
    }

    private static @NotNull List<Player> getAllPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> (Player) player).toList();
    }

    /**
     * セレクター「@e」に相当するセレクターのテンプレート
     */
    public static final Builder<Entity> E = new Builder<>() {
        @Override
        @NotNull List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Entity> entities = EntitySelector.getAllEntities();
            return SelectorSortOrder.ARBITRARY.sort(entities, stack);
        }

        @Override
        @NotNull List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull List<SelectorArgument> arguments) {
            return entities;
        }
    };

    /**
     * セレクター「@s」に相当するセレクターのテンプレート
     */
    public static final Builder<Entity> S = new Builder<>() {
        @Override
        @NotNull List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            return stack.hasExecutor() ? List.of(stack.getExecutor()) : List.of();
        }

        @Override
        @NotNull List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull List<SelectorArgument> arguments) {
            return entities;
        }
    };

    /**
     * セレクター「@a」に相当するセレクターのテンプレート
     */
    public static final Builder<Player> A = new Builder<>() {
        @Override
        @NotNull List<Player> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Player> players = EntitySelector.getAllPlayers();
            return SelectorSortOrder.ARBITRARY.sort(players, stack);
        }

        @Override
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack, @NotNull List<SelectorArgument> arguments) {
            return entities;
        }
    };

    /**
     * セレクター「@p」に相当するセレクターのテンプレート
     */
    public static final Builder<Player> P = new Builder<>() {
        @Override
        @NotNull List<Player> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Player> players = EntitySelector.getAllPlayers()
                .stream()
                .filter(player -> !player.isDead())
                .toList();

            return SelectorSortOrder.NEAREST.sort(players, stack);
        }

        @Override
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack, @NotNull List<SelectorArgument> arguments) {
            if (arguments.stream().anyMatch(argument -> argument.getId().equals(SelectorArgument.LIMIT.getId()))) {
                return entities;
            }
            else return List.of(entities.getFirst());
        }
    };

    /**
     * セレクター「@r」に相当するセレクターのテンプレート
     */
    public static final Builder<Player> R = new Builder<>() {
        @Override
        @NotNull List<Player> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Player> players = new ArrayList<>(
                EntitySelector.getAllPlayers()
                    .stream()
                    .filter(player -> !player.isDead())
                    .toList()
            );

            return SelectorSortOrder.RANDOM.sort(players, stack);
        }

        @Override
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack, @NotNull List<SelectorArgument> arguments) {
            if (arguments.stream().anyMatch(argument -> argument.getId().equals(SelectorArgument.LIMIT.getId()))) {
                return entities;
            }
            else return List.of(entities.getFirst());
        }
    };

    /**
     * セレクター「@n」に相当するセレクターのテンプレート
     */
    public static final Builder<Entity> N = new Builder<>() {
        @Override
        @NotNull
        List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            return SelectorSortOrder.NEAREST.sort(EntitySelector.getAllEntities(), stack);
        }

        @Override
        @NotNull
        List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack, @NotNull List<SelectorArgument> arguments) {
            if (arguments.stream().anyMatch(argument -> argument.getId().equals(SelectorArgument.LIMIT.getId()))) {
                return entities;
            }
            else return List.of(entities.getFirst());
        }
    };

    /**
     * 引数未設定の状態のエンティティセレクターを作成するためのクラス
     */
    public static abstract class Builder<T extends Entity> {
        private Builder() {}

        abstract @NotNull List<T> getTargetCandidates(@NotNull SourceStack stack);

        abstract @NotNull List<T> selectorSpecificModifier(@NotNull List<T> entities, @NotNull SourceStack stack, @NotNull List<SelectorArgument> arguments);

        /**
         * 新しくセレクターを作成します。
         */
        protected @NotNull EntitySelector<T> build() {
            return new EntitySelector<>(this);
        }

        /**
         * セレクターに引数を追加してセレクターを作成します。
         * @param modifier セレクター引数の種類
         * @param value セレクター引数に渡す値
         * @return 作成されたセレクター
         */
        public <U> @NotNull EntitySelector<T> arg(@NotNull SelectorArgument.Builder<U> modifier, @NotNull U value) {
            return build().arg(modifier, value);
        }

        /**
         * セレクターに負の引数を追加してセレクターを作成します。
         * @param modifier セレクター引数の種類
         * @param value セレクター引数に渡す値
         * @return 作成されたセレクター
         */
        public <U> @NotNull EntitySelector<T> notArg(@NotNull SelectorArgument.Builder<U> modifier, @NotNull U value) {
            return build().notArg(modifier, value);
        }
    }
}
