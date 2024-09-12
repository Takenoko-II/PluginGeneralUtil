package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntitySelector<T extends Entity> {
    private final List<EntitySelectorModifier<T>> modifiers = new ArrayList<>();

    private final Builder<T> builder;

    private EntitySelector(@NotNull Builder<T> builder) {
        this.builder = builder;
    }

    public <U> @NotNull EntitySelector<T> argument(@NotNull EntitySelectorModifier.Builder<? extends Entity, U> modifier, @NotNull U value) {
        modifiers.add((EntitySelectorModifier<T>) modifier.build(value));
        modifiers.sort((a, b) -> b.getPriority() - a.getPriority());
        return this;
    }

    private @NotNull List<T> modifier(@NotNull List<T> entities, @NotNull SourceStack stack) {
        final SourceStack copy = stack.copy();
        List<T> out = entities;

        // xyz -> sort -> limit
        for (final EntitySelectorModifier<T> modifier : modifiers) {
            out = modifier.modify(out, copy);
        }

        return out;
    }

    public @NotNull List<T> getEntities(@NotNull SourceStack stack) {
        return builder.selectorSpecificModifier(modifier(builder.getTargetCandidates(stack), stack), stack);
    }

    private static @NotNull List<Entity> getAllEntities() {
        return Bukkit.getServer().getWorlds().stream()
            .flatMap(world -> world.getEntities().stream())
            .toList();
    }

    private static @NotNull List<Player> getAllPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> (Player) player).toList();
    }

    public static final Builder<Entity> E = new Builder<>() {
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

    public static final Builder<Entity> S = new Builder<>() {
        @Override
        @NotNull List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            return stack.getExecutor() == null ? List.of() : List.of(stack.getExecutor());
        }

        @Override
        @NotNull List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
            return entities;
        }
    };

    public static final Builder<Player> A = new Builder<>() {
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
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };

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
        @NotNull List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };

    public static final Builder<Entity> N = new Builder<>() {
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

    public static abstract class Builder<T extends Entity> {
        private Builder() {}

        abstract @NotNull List<T> getTargetCandidates(@NotNull SourceStack stack);

        abstract @NotNull List<T> selectorSpecificModifier(@NotNull List<T> entities, @NotNull SourceStack stack);

        public @NotNull EntitySelector<T> build() {
            return new EntitySelector<>(this);
        }
    }
}
