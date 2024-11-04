package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.scoreboard.ScoreObjective;
import com.gmail.subnokoii78.util.scoreboard.ScoreboardUtils;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * スコアホルダーを表現するクラス
 */
public abstract class ScoreHolder {
    protected abstract @Nullable Integer getScore(@NotNull String objectiveId, @NotNull SourceStack stack);

    protected abstract void setScore(@NotNull String objectiveId, @NotNull SourceStack stack, int value);

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
    public static @NotNull ScoreHolder of(@NotNull EntitySelector.Builder<? extends Entity> selector) {
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
        protected @Nullable Integer getScore(@NotNull String objective, @NotNull SourceStack stack) {
            if (!ScoreboardUtils.isRegistered(objective)) return null;
            return ScoreboardUtils.getObjective(objective).getScore(stack.getEntities(selector).getFirst());
        }

        @Override
        protected void setScore(@NotNull String objective, @NotNull SourceStack stack, int value) {
            if (!ScoreboardUtils.isRegistered(objective)) return;
            final ScoreObjective o = ScoreboardUtils.getObjective(objective);
            stack.getEntities(selector).forEach(entity -> o.setScore(entity, value));
        }
    }

    private static final class StringScoreHolder extends ScoreHolder {
        private final String name;

        private StringScoreHolder(@NotNull String name) {
            this.name = name;
        }

        @Override
        protected @Nullable Integer getScore(@NotNull String objective, @NotNull SourceStack stack) {
            if (!ScoreboardUtils.isRegistered(objective)) return null;
            return ScoreboardUtils.getObjective(objective).getScore(name);
        }

        @Override
        protected void setScore(@NotNull String objective, @NotNull SourceStack stack, int value) {
            if (!ScoreboardUtils.isRegistered(objective)) return;
            ScoreboardUtils.getObjective(objective).setScore(name, value);
        }
    }
}
