package com.gmail.subnokoii78.util.vector.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EntityAnchor {
    FEET {
        @NotNull
        @Override
        Vector3Builder getEntityAnchor(@Nullable Entity entity) {
            if (entity == null) return new Vector3Builder();
            else return Vector3Builder.from(entity);
        }
    },

    EYES {
        @NotNull Vector3Builder getEntityAnchor(@Nullable Entity entity) {
            if (entity == null) return new Vector3Builder();
            else if (entity instanceof LivingEntity livingEntity) {
                return new Vector3Builder(0, livingEntity.getEyeHeight(), 0);
            }
            else return new Vector3Builder();
        }
    };

    abstract @NotNull Vector3Builder getEntityAnchor(@Nullable Entity entity);
}
