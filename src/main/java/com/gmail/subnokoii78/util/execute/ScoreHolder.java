package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.scoreboard.ScoreboardUtils;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * スコアホルダーを表現するクラス
 */
public abstract class ScoreHolder {
    abstract @Nullable Integer getScore(@NotNull String objectiveId, @NotNull SourceStack stack);

    abstract void setScore(@NotNull String objectiveId, @NotNull SourceStack stack, int value);

    /**
     * 単一のエンティティを示すセレクターからエンティティのスコアホルダーを取得します。
     * @param selector セレクター
     * @return スコアホルダー
     */
    public static @NotNull ScoreHolder of(@NotNull EntitySelector<? extends Entity> selector) {
        return new EntityScoreHolder(selector);
    }

    /**
     * 単一のエンティティを示すセレクターからエンティティのスコアホルダーを取得します。
     * @param selector セレクター
     * @return スコアホルダー
     */
    public static @NotNull ScoreHolder of(@NotNull EntitySelector.Provider<? extends Entity> selector) {
        return new EntityScoreHolder(selector.build());
    }

    /**
     * 文字列をスコアホルダーとして取得します。
     * @param name フェイクプレイヤー名
     * @return スコアホルダー
     */
    public static @NotNull ScoreHolder of(@NotNull String name) {
        return new StringScoreHolder(name);
    }

    /**
     * エンティティをスコアホルダーとして取得します。
     * @param entity エンティティ
     * @return スコアホルダー
     */
    public static @NotNull ScoreHolder of(@NotNull Entity entity) {
        return new StringScoreHolder(entity.getUniqueId().toString());
    }

    private static final class EntityScoreHolder extends ScoreHolder {
        private final EntitySelector<? extends Entity> selector;

        private EntityScoreHolder(@NotNull EntitySelector<? extends Entity> selector) {
            if (selector.isSingle()) {
                this.selector = selector;
            }
            else {
                throw new IllegalArgumentException("セレクターは単一のエンティティを指定する必要があります");
            }
        }

        @Override
        @Nullable Integer getScore(@NotNull String objectiveId, @NotNull SourceStack stack) {
            final ScoreboardUtils.Objective objective = ScoreboardUtils.getObjective(objectiveId);

            if (objective == null) return null;

            return objective.getScore(stack.getEntities(selector).getFirst());
        }

        @Override
        void setScore(@NotNull String objectiveId, @NotNull SourceStack stack, int value) {
            final ScoreboardUtils.Objective objective = ScoreboardUtils.getObjective(objectiveId);

            if (objective == null) return;

            stack.getEntities(selector).forEach(entity -> objective.setScore(entity, value));
        }
    }

    private static final class StringScoreHolder extends ScoreHolder {
        private final String name;

        private StringScoreHolder(@NotNull String name) {
            this.name = name;
        }

        @Override
        @Nullable Integer getScore(@NotNull String objectiveId, @NotNull SourceStack stack) {
            final ScoreboardUtils.Objective objective = ScoreboardUtils.getObjective(objectiveId);

            if (objective == null) return null;

            return objective.getScore(name);
        }

        @Override
        void setScore(@NotNull String objectiveId, @NotNull SourceStack stack, int value) {
            final ScoreboardUtils.Objective objective = ScoreboardUtils.getObjective(objectiveId);

            if (objective == null) return;

            objective.setScore(name, value);
        }
    }
}
