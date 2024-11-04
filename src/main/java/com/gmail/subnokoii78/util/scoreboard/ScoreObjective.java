package com.gmail.subnokoii78.util.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ScoreObjective {
    private final Objective objective;

    ScoreObjective(@NotNull Objective objective) {
        this.objective = objective;
    }

    public boolean hasScore(@NotNull Entity entity) {
        return objective.getScoreFor(entity).isScoreSet();
    }

    public boolean hasScore(@NotNull String name) {
        return objective.getScore(name).isScoreSet();
    }

    public int getScore(@NotNull Entity entity) {
        return objective.getScoreFor(entity).getScore();
    }

    public int getScore(@NotNull String name) {
        return objective.getScore(name).getScore();
    }

    public @NotNull ScoreObjective setScore(@NotNull Entity entity, int value) {
        objective.getScoreFor(entity).setScore(value);
        return this;
    }

    public @NotNull ScoreObjective setScore(@NotNull String name, int value) {
        objective.getScore(name).setScore(value);
        return this;
    }

    public @NotNull ScoreObjective addScore(@NotNull Entity entity, int value) {
        setScore(entity, getScore(entity) + value);
        return this;
    }

    public @NotNull ScoreObjective addScore(@NotNull String name, int value) {
        setScore(name, getScore(name) + value);
        return this;
    }

    public @NotNull ScoreObjective subtractScore(@NotNull Entity entity, int value) {
        setScore(entity, getScore(entity) - value);
        return this;
    }

    public @NotNull ScoreObjective subtractScore(@NotNull String name, int value) {
        setScore(name, getScore(name) - value);
        return this;
    }

    public @NotNull ScoreObjective multiplyScore(@NotNull Entity entity, int value) {
        setScore(entity, getScore(entity) * value);
        return this;
    }

    public @NotNull ScoreObjective multiplyScore(@NotNull String name, int value) {
        final int subtrahend = getScore(name) * value;
        setScore(name, subtrahend);
        return this;
    }

    public @NotNull ScoreObjective divideScore(@NotNull Entity entity, int value) {
        if (value == 0) return this;
        setScore(entity, getScore(entity) / value);
        return this;
    }

    public @NotNull ScoreObjective divideScore(@NotNull String name, int value) {
        if (value == 0) return this;
        setScore(name, getScore(name) / value);
        return this;
    }

    public @NotNull ScoreObjective resetScore(@NotNull Entity entity) {
        objective.getScoreFor(entity).resetScore();
        return this;
    }

    public @NotNull ScoreObjective resetScore(@NotNull String name) {
        objective.getScore(name).resetScore();
        return this;
    }

    public @NotNull String getName() {
        return objective.getName();
    }

    public @NotNull Criteria getCriteria() {
        return objective.getTrackedCriteria();
    }

    public @NotNull Component getDisplayName() {
        return objective.displayName();
    }

    public void setDisplayName(@NotNull Component displayName) {
        objective.displayName(displayName);
    }

    public boolean isDisplayed() {
        return objective.getDisplaySlot() != null;
    }

    public @NotNull DisplaySlot getDisplaySlot() throws IllegalStateException {
        final DisplaySlot displaySlot = objective.getDisplaySlot();
        if (displaySlot == null) {
            throw new IllegalStateException("このオブジェクティブは現在表示されていません");
        }
        else return displaySlot;
    }

    public void setDisplaySlot(@Nullable DisplaySlot displaySlot) {
        objective.setDisplaySlot(displaySlot);
    }
}
