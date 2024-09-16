package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class SelectorSortOrder {
    private SelectorSortOrder() {}

    public abstract <T extends Entity> @NotNull List<T> sort(@NotNull List<T> entities, @NotNull SourceStack stack);

    public static final SelectorSortOrder NEAREST = new SelectorSortOrder() {
        @Override
        public <T extends Entity> @NotNull List<T> sort(@NotNull List<T> entities, @NotNull SourceStack stack) {
            final List<T> out = new ArrayList<>(entities);

            out.sort((o1, o2) -> {
                final Vector3Builder location = stack.getPosition();
                final double distance1 = location.getDistanceTo(Vector3Builder.from(o1));
                final double distance2 = location.getDistanceTo(Vector3Builder.from(o2));
                return Double.compare(distance1, distance2);
            });

            return out;
        }
    };

    public static final SelectorSortOrder FURTHEST = new SelectorSortOrder() {
        @Override
        public <T extends Entity> @NotNull List<T> sort(@NotNull List<T> entities, @NotNull SourceStack stack) {
            final List<T> out = new ArrayList<>(entities);

            out.sort((o1, o2) -> {
                final Vector3Builder location = stack.getPosition();
                final double distance1 = location.getDistanceTo(Vector3Builder.from(o1));
                final double distance2 = location.getDistanceTo(Vector3Builder.from(o2));
                return Double.compare(distance2, distance1);
            });

            return out;
        }
    };

    public static final SelectorSortOrder ARBITRARY = new SelectorSortOrder() {
        @Override
        public <T extends Entity> @NotNull List<T> sort(@NotNull List<T> entities, @NotNull SourceStack stack) {
            final List<T> out = new ArrayList<>(entities);

            out.sort(Comparator.comparingDouble(Entity::getTicksLived));

            return out;
        }
    };

    public static final SelectorSortOrder RANDOM = new SelectorSortOrder() {
        @Override
        public <T extends Entity> @NotNull List<T> sort(@NotNull List<T> entities, @NotNull SourceStack stack) {
            final List<T> out = new ArrayList<>(entities);

            out.sort(Comparator.comparingDouble(o -> Math.random()));

            return out;
        }
    };
}
