package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public final Align align = new Align();

    public final Anchored anchored = new Anchored();

    public final In in = new In();

    public final class As {
        private As() {}

        public <T extends Entity> @NotNull Execute $(@NotNull EntitySelector<T> selector) {
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

        public <T extends Entity> @NotNull Execute $(@NotNull EntitySelector<T> selector) {
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
            return redirect(stack -> {
                stack.write(stack.readCoordinates(input));
                stack.write(EntityAnchor.FEET);
            });
        }

        public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector<T> selector) {
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

        public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector<T> selector) {
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
                        .getDirectionTo(
                            Vector3Builder.from(entity)
                                .add(anchor.getEntityAnchor(entity))
                        );
                    copy.write(direction.getRotation2d());
                    return copy;
                })
                .toList()
            );
        }
    }

    public final class Align {
        private Align() {}

        public @NotNull Execute $(@NotNull String axes) {
            return redirect(stack -> stack.write(SourceStack.floorAxis(axes, stack.getLocation())));
        }
    }

    public final class Anchored {
        private Anchored() {}

        public @NotNull Execute $(@NotNull EntityAnchor anchor) {
            return redirect(stack -> stack.write(anchor));
        }
    }

    public final class In {
        private In() {}

        public @NotNull Execute $(@NotNull DimensionProvider dimension) {
            return redirect(stack -> stack.write(dimension.getWorld()));
        }
    }

    public final class If {
        private If() {}

        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector<T> selector) {
            return fork(stack -> {
                if (stack.getEntities(selector).isEmpty()) {
                    return List.of();
                }
                else return List.of(stack);
            });
        }

        public @NotNull Execute block(@NotNull String location, @NotNull Material blockType) {
            return fork(stack -> {
                final Block block = stack.getDimension().getBlockAt(
                    stack.readCoordinates(location)
                        .withWorld(stack.getDimension())
                );

                if (block.getType().equals(blockType)) {
                    return List.of();
                }
                else return List.of(stack);
            });
        }


    }
}
