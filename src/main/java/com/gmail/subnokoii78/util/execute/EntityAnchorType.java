package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EntityAnchorType {
    /**
     * エンティティアンカーを足先にするオプション
     */
    FEET("feet") {
        @Override
        @NotNull Vector3Builder provideOffset(@Nullable Entity entity) {
            return new Vector3Builder();
        }
    },

    /**
     * エンティティアンカーを目元にするオプション
     */
    EYES("eyes") {
        @Override
        @NotNull Vector3Builder provideOffset(@Nullable Entity entity) {
            if (entity == null) return new Vector3Builder();
            else if (entity instanceof LivingEntity livingEntity) {
                return new Vector3Builder(0, livingEntity.getEyeHeight(), 0);
            }
            else return new Vector3Builder();
        }
    };

    private final String id;

    EntityAnchorType(@NotNull String id) {
        this.id = id;
    }

    /**
     * IDを取得します。
     * @return ID
     */
    public @NotNull String getId() {
        return id;
    }

    abstract @NotNull Vector3Builder provideOffset(@Nullable Entity entity);
}
