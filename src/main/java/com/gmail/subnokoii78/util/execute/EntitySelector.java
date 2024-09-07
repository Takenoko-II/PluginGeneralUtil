package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class EntitySelector<T extends Entity> {
    private final Set<EntitySelectorModifier<T>> modifiers = new HashSet<>();

    private EntitySelector() {}

    abstract @NotNull List<T> getTargetCandidates(@NotNull SourceStack stack);

    private @NotNull List<T> modifier(@NotNull List<T> entities, @NotNull SourceStack stack) {
        final SourceStack copy = stack.copy();
        List<T> out = entities;

        // xyz -> sort -> limit

        // ModifierPriorityの追加
        // Set -> List

        for (final EntitySelectorModifier<T> modifier : modifiers) {
            if (modifier instanceof EntitySelectorModifier.EntitySelectorXYZ xyz) {
                copy.write(xyz.getCoordinates());
                break;
            }
        }

        for (final EntitySelectorModifier<T> modifier : modifiers) {
            if (modifier instanceof EntitySelectorModifier.EntitySelectorXYZ) continue;

            out = modifier.modify(out, copy);
        }

        return out;
    }

    abstract @NotNull List<T> selectorSpecificModifier(@NotNull List<T> entities, @NotNull SourceStack stack);

    public @NotNull List<T> getEntities(@NotNull SourceStack stack) {
        return selectorSpecificModifier(modifier(getTargetCandidates(stack), stack), stack);
    }

    public <U> @NotNull EntitySelector<T> argument(@NotNull EntitySelectorModifier.Builder<T, U> modifier, @NotNull U value) {
        modifiers.add(modifier.build(value));
        return this;
    }

    private static @NotNull List<Entity> getAllEntities() {
        return Bukkit.getServer().getWorlds().stream()
            .flatMap(world -> world.getEntities().stream())
            .toList();
    }

    private static @NotNull List<Player> getAllPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> (Player) player).toList();
    }

    public static final EntitySelector<Entity> E = new EntitySelector<>() {
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

    public static final EntitySelector<Entity> S = new EntitySelector<>() {
        @NotNull
        @Override
        List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            return stack.getExecutor() == null ? List.of() : List.of(stack.getExecutor());
        }

        @NotNull
        @Override
        List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
            return stack.getExecutor() == null ? List.of() : List.of(stack.getExecutor());
        }
    };

    public static final EntitySelector<Player> A = new EntitySelector<>() {
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

    public static final EntitySelector<Player> P = new EntitySelector<>() {
        @NotNull
        @Override
        List<Player> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Player> players = EntitySelector.getAllPlayers()
                .stream()
                .filter(player -> !player.isDead())
                .toList();

            return SelectorSortOrder.NEAREST.sort(players, stack);
        }

        @NotNull
        @Override
        List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };

    public static final EntitySelector<Player> R = new EntitySelector<>() {
        @NotNull
        @Override
        List<Player> getTargetCandidates(@NotNull SourceStack stack) {
            final List<Player> players = new ArrayList<>(
                EntitySelector.getAllPlayers()
                    .stream()
                    .filter(player -> !player.isDead())
                    .toList()
            );

            return SelectorSortOrder.RANDOM.sort(players, stack);
        }

        @NotNull
        @Override
        List<Player> selectorSpecificModifier(@NotNull List<Player> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };

    public static final EntitySelector<Entity> N = new EntitySelector<>() {
        @NotNull
        @Override
        List<Entity> getTargetCandidates(@NotNull SourceStack stack) {
            return SelectorSortOrder.NEAREST.sort(EntitySelector.getAllEntities(), stack);
        }

        @NotNull
        @Override
        List<Entity> selectorSpecificModifier(@NotNull List<Entity> entities, @NotNull SourceStack stack) {
            return List.of(entities.getFirst());
        }
    };
}
