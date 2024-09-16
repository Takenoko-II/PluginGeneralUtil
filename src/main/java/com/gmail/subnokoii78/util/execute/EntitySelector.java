package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntitySelector<T extends Entity> {
    private final List<SelectorArgument<T>> modifiers = new ArrayList<>();

    private final Provider<T> provider;

    private EntitySelector(@NotNull Provider<T> builder) {
        this.provider = builder;
    }

    /**
     * セレクターが単一のエンティティを示すことが保障されていることをテストします。
     * @return @p, @n, @e[limit=1]などはtrue
     */
    public boolean isSingle() {
        for (final SelectorArgument<T> modifier : modifiers) {
            if (modifier.getId().equals(SelectorArgument.LIMIT.getId())) {
                return true;
            }
        }

        return provider.equals(S) || provider.equals(P) || provider.equals(N);
    }

    /**
     * セレクターに引数を追加します。
     * @param modifier セレクター引数の種類
     * @param value セレクター引数に渡す値
     * @return thisをそのまま返す
     */
    public <U> @NotNull EntitySelector<T> argument(@NotNull SelectorArgument.Builder<? extends Entity, U> modifier, @NotNull U value) {
        modifiers.add((SelectorArgument<T>) modifier.build(value));
        modifiers.sort((a, b) -> b.getPriority() - a.getPriority());
        return this;
    }

    private @NotNull List<T> modifier(@NotNull List<T> entities, @NotNull SourceStack stack) {
        final SourceStack copy = stack.copy();
        List<T> out = entities;

        // xyz -> sort -> limit
        for (final SelectorArgument<T> modifier : modifiers) {
            out = modifier.modify(out, copy);
        }

        return out;
    }

    @NotNull List<T> getEntities(@NotNull SourceStack stack) {
        return provider.selectorSpecificModifier(modifier(provider.getTargetCandidates(stack), stack), stack);
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
    public static final Provider<Entity> E = new Provider<>() {
        @Override
        @NotNull List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Entity> entities = EntitySelector.getAllEntities();
            return SelectorSortOrder.ARBITRARY.sort(entities, stack);
        }

        @Override
        @NotNull List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
            return entities;
        }
    };

    /**
     * セレクター「@s」に相当するセレクターのテンプレート
     */
    public static final Provider<Entity> S = new Provider<>() {
        @Override
        @NotNull List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            return stack.getExecutor() == null ? List.of() : List.of(stack.getExecutor());
        }

        @Override
        @NotNull List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
            return entities;
        }
    };

    /**
     * セレクター「@a」に相当するセレクターのテンプレート
     */
    public static final Provider<Player> A = new Provider<>() {
        @Override
        @NotNull List<Player> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Player> players = EntitySelector.getAllPlayers();
            return SelectorSortOrder.ARBITRARY.sort(players, stack);
        }

        @Override
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack) {
            return entities;
        }
    };

    /**
     * セレクター「@p」に相当するセレクターのテンプレート
     */
    public static final Provider<Player> P = new Provider<>() {
        @Override
        @NotNull List<Player> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Player> players = EntitySelector.getAllPlayers()
                .stream()
                .filter(player -> !player.isDead())
                .toList();

            return SelectorSortOrder.NEAREST.sort(players, stack);
        }

        @Override
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };

    /**
     * セレクター「@r」に相当するセレクターのテンプレート
     */
    public static final Provider<Player> R = new Provider<>() {
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
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };

    /**
     * セレクター「@n」に相当するセレクターのテンプレート
     */
    public static final Provider<Entity> N = new Provider<>() {
        @Override
        @NotNull
        List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            return SelectorSortOrder.NEAREST.sort(EntitySelector.getAllEntities(), stack);
        }

        @Override
        @NotNull
        List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };

    public static abstract class Provider<T extends Entity> {
        private Provider() {}

        abstract @NotNull List<T> getTargetCandidates(@NotNull SourceStack stack);

        abstract @NotNull List<T> selectorSpecificModifier(@NotNull List<T> entities, @NotNull SourceStack stack);

        /**
         * 新しくセレクターを作成します。
         */
        public @NotNull EntitySelector<T> create() {
            return new EntitySelector<>(this);
        }
    }
}
