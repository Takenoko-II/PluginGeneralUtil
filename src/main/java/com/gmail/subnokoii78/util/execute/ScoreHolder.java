package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.scoreboard.ScoreboardUtils;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ScoreHolder {
    private final String id;

    public ScoreHolder(@NotNull String id) {
        this.id = id;
    }

    public ScoreHolder(@NotNull Entity entity) {
        this.id = entity.getUniqueId().toString();
    }

    public ScoreHolder(@NotNull EntitySelector<? extends Entity> selector, @NotNull SourceStack stack) {
        if (!selector.isSingle()) throw new IllegalArgumentException("セレクターは単一のエンティティを指定する必要があります");

        this.id = selector.getEntities(stack).getFirst().getUniqueId().toString();
    }

    public @Nullable Integer getScore(@NotNull String objectiveId) {
        final ScoreboardUtils.Objective objective = ScoreboardUtils.getObjective(objectiveId);

        if (objective == null) return null;

        return objective.getScore(id);
    }
}
