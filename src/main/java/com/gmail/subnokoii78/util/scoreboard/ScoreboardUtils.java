package com.gmail.subnokoii78.util.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class ScoreboardUtils {
    private static final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    public static @NotNull ScoreObjective createObjective(@NotNull String name) {
        return createObjective(name, Criteria.DUMMY);
    }

    public static @NotNull ScoreObjective createObjective(@NotNull String name, @NotNull Criteria criteria) {
        return createObjective(name, criteria, Component.text(name));
    }

    public static @NotNull ScoreObjective createObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull Component displayName) {
        if (isRegistered(name)) {
            throw new IllegalArgumentException("既にその名前のオブジェクティブは登録されています");
        }

        return new ScoreObjective(scoreboard.registerNewObjective(name, criteria, displayName));
    }

    public static boolean isRegistered(@NotNull String name) {
        return scoreboard.getObjective(name) != null;
    }

    public static @NotNull ScoreObjective getObjective(String name) throws IllegalArgumentException {
        final Objective objective = scoreboard.getObjective(name);
        if (objective == null) {
            throw new IllegalArgumentException("その名前のオブジェクティブは登録されていません");
        }
        else return new ScoreObjective(objective);
    }

    public static @NotNull ScoreObjective getOrCreateObjective(@NotNull String name) {
        return isRegistered(name) ? getObjective(name) : createObjective(name);
    }

    public static @NotNull ScoreObjective getOrCreateObjective(@NotNull String name, @NotNull Criteria criteria) {
        return isRegistered(name) ? getObjective(name) : createObjective(name, criteria);
    }

    public static @NotNull ScoreObjective getOrCreateObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull Component displayName) {
        return isRegistered(name) ? getObjective(name) : createObjective(name, criteria, displayName);
    }

    public static @NotNull Set<ScoreObjective> getAllObjectives() {
        return scoreboard
            .getObjectives()
            .stream()
            .map(ScoreObjective::new)
            .collect(Collectors.toSet());
    }

    public static void removeObjective(@NotNull String name) {
        final Objective objective = scoreboard.getObjective(name);
        if (objective == null) return;
        objective.unregister();
    }

}
