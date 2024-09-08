package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EntityAnchor {
    FEET("feet") {
        @Override
        public @NotNull Vector3Builder getOffset(@Nullable Entity entity) {
            return new Vector3Builder();
        }
    },

    EYES("eyes") {
        @Override
        public @NotNull Vector3Builder getOffset(@Nullable Entity entity) {
            if (entity == null) return new Vector3Builder();
            else if (entity instanceof LivingEntity livingEntity) {
                return new Vector3Builder(0, livingEntity.getEyeHeight(), 0);
            }
            else return new Vector3Builder();
        }
    };

    private final String id;

    EntityAnchor(@NotNull String id) {
        this.id = id;
    }

    public @NotNull String getId() {
        return id;
    }

    public abstract @NotNull Vector3Builder getOffset(@Nullable Entity entity);
}
