package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EntityAnchor {
    FEET {
        @Override
        public @NotNull Vector3Builder getEntityAnchor(@Nullable Entity entity) {
            return new Vector3Builder();
        }
    },

    EYES {
        @Override
        public @NotNull Vector3Builder getEntityAnchor(@Nullable Entity entity) {
            if (entity == null) return new Vector3Builder();
            else if (entity instanceof LivingEntity livingEntity) {
                return new Vector3Builder(0, livingEntity.getEyeHeight(), 0);
            }
            else return new Vector3Builder();
        }
    };

    public abstract @NotNull Vector3Builder getEntityAnchor(@Nullable Entity entity);
}
