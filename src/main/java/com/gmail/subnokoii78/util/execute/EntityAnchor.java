package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * ソーススタックと結びついたエンティティアンカーを表現するクラス
 */
public final class EntityAnchor {
    private final SourceStack stack;

    private Type type = EntityAnchor.FEET;

    EntityAnchor(@NotNull SourceStack stack) {
        this.stack = stack;
    }

    /**
     * エンティティアンカーの種類を取得します。
     * @return eyesまたはfeet
     */
    public @NotNull Type getType() {
        return type;
    }

    void setType(@NotNull Type provider) {
        this.type = provider;
    }

    /**
     * エンティティアンカーによる実行座標のオフセットを取得します。
     * @return アンカーオフセット
     */
    public @NotNull Vector3Builder getOffset() {
        return type.getOffset(stack.hasExecutor() ? stack.getExecutor() : null);
    }

    public static abstract class Type {
        private static final Map<String, Type> types = new HashMap<>();

        private final String id;

        protected Type(@NotNull String id) {
            this.id = id;
            types.put(id, this);
        }

        public @NotNull String getId() {
            return id;
        }

        protected abstract @NotNull Vector3Builder getOffset(@Nullable Entity entity);

        public static @NotNull Type get(@NotNull String id) throws IllegalArgumentException {
            return Objects.requireNonNull(types.get(id), "無効なIDです");
        }
    }

    public static final Type EYES = new Type("eyes") {
        @Override
        protected @NotNull Vector3Builder getOffset(@Nullable Entity entity) {
            return switch (entity) {
                case LivingEntity livingEntity -> new Vector3Builder(0, livingEntity.getEyeHeight(), 0);
                case null, default -> new Vector3Builder();
            };
        }
    };

    public static final Type FEET = new Type("feet") {
        @Override
        protected @NotNull Vector3Builder getOffset(@Nullable Entity entity) {
            return new Vector3Builder();
        }
    };
}
