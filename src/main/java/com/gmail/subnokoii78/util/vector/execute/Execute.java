package com.gmail.subnokoii78.util.vector.execute;

import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Execute {
    private final Execute that = this;

    private final List<SourceStack> stacks = new ArrayList<>();

    private @NotNull Execute redirect(Consumer<SourceStack> modifier) {
        stacks.forEach(modifier);
        return this;
    }

    private @NotNull Execute fork(Function<SourceStack, List<SourceStack>> modifier) {
        final List<SourceStack> newStacks = new ArrayList<>();
        for (final SourceStack stack : stacks) {
            newStacks.addAll(modifier.apply(stack.copy()));
        }
        stacks.clear();
        stacks.addAll(newStacks);
        return this;
    }

    public Execute(@NotNull SourceStack stack) {
        this.stacks.add(new SourceStack());
    }

    public final As as = new As();

    public final At at = new At();

    public final Positioned positioned = new Positioned();

    public final Rotated rotated = new Rotated();

    public final Facing facing = new Facing();

    public final Anchored anchored = new Anchored();

    public final In in = new In();

    public final class As {
        private As() {}

        private <T extends Entity> @NotNull Execute $(@NotNull EntitySelector<T> selector) {
            return fork(stack -> stack.getEntities(selector)
                .stream()
                .map(entity -> {
                    final SourceStack copy = stack.copy();
                    copy.write(entity);
                    return copy;
                })
                .toList()
            );
        }
    }

    public final class At {
        private At() {}

        private <T extends Entity> @NotNull Execute $(@NotNull EntitySelector<T> selector) {
            return fork(stack -> stack.getEntities(selector)
                .stream()
                .map(entity -> {
                    final SourceStack copy = stack.copy();
                    copy.write(Vector3Builder.from(entity));
                    copy.write(DualAxisRotationBuilder.from(entity));
                    copy.write(entity.getWorld());
                    return copy;
                })
                .toList()
            );
        }
    }

    public final class Positioned {
        private Positioned() {}

        public @NotNull Execute $(@NotNull String input) {
            return redirect(stack -> stack.write(stack.readCoordinates(input)));
        }

        private <T extends Entity> @NotNull Execute as(@NotNull EntitySelector<T> selector) {
            return fork(stack -> stack.getEntities(selector)
                .stream()
                .map(entity -> {
                    final SourceStack copy = stack.copy();
                    copy.write(Vector3Builder.from(entity));
                    return copy;
                })
                .toList()
            );
        }
    }

    public final class Rotated {
        private Rotated() {}

        public @NotNull Execute $(@NotNull String input) {
            return redirect(stack -> stack.write(stack.readAngles(input)));
        }

        private <T extends Entity> @NotNull Execute as(@NotNull EntitySelector<T> selector) {
            return fork(stack -> stack.getEntities(selector)
                .stream()
                .map(entity -> {
                    final SourceStack copy = stack.copy();
                    copy.write(DualAxisRotationBuilder.from(entity));
                    return copy;
                })
                .toList()
            );
        }
    }

    public final class Facing {
        private Facing() {}

        public @NotNull Execute $(@NotNull String input) {
            return redirect(stack -> {
                final Vector3Builder direction = stack.getLocation().getDirectionTo(stack.readCoordinates(input));
                stack.write(direction.getRotation2d());
            });
        }

        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector<T> selector, @NotNull EntityAnchor anchor) {
            return fork(stack -> stack.getEntities(selector)
                .stream()
                .map(entity -> {
                    final SourceStack copy = stack.copy();
                    final Vector3Builder direction = copy.getLocation()
                        .add(copy.getEntityAnchor())
                        .getDirectionTo(Vector3Builder.from(entity).add(anchor.getEntityAnchor(entity)));
                    copy.write(direction.getRotation2d());
                    return copy;
                })
                .toList()
            );
        }
    }

    public final class Anchored {
        private Anchored() {}

        public @NotNull Execute $(@NotNull EntityAnchor anchor) {
            stacks.forEach(stack -> stack.anchored(anchor));
            return that;
        }
    }

    public final class In {
        private In() {}

        private <T extends Entity> @NotNull Execute $(@NotNull WorldType worldType) {
            return redirect(stack -> {
                stack.write(Bukkit.getWorld(worldType.name()));
            });
        }
    }
}
