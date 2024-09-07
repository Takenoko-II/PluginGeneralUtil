package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Execute {
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

    public final As AS = new As();

    public final At AT = new At();

    public final Positioned POSITIONED = new Positioned();

    public final Rotated ROTATED = new Rotated();

    public final Facing FACING = new Facing();

    public final Align ALIGN = new Align();

    public final Anchored ANCHORED = new Anchored();

    public final In IN = new In();

    public final If IF = new If();

    public final Unless UNLESS = new Unless();

    public final On ON = new On();

    public final Summon SUMMON = new Summon();

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
            return redirect(stack -> stack.floorAxis(axes));
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
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute predicate(Predicate<SourceStack> predicate) {
            return fork(stack -> {
                final SourceStack copy = stack.copy();

                if (predicate.test(copy)) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }
    }

    public final class Unless {
        private Unless() {}

        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector<T> selector) {
            return fork(stack -> {
                if (!stack.getEntities(selector).isEmpty()) {
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

                if (!block.getType().equals(blockType)) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute predicate(Predicate<SourceStack> predicate) {
            return fork(stack -> {
                final SourceStack copy = stack.copy();

                if (!predicate.test(copy)) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }
    }

    public final class On {
        private On() {}

        public @NotNull Execute passengers() {
            return fork(stack -> {
                final Entity executor = stack.getExecutor();
                if (executor == null) return List.of();

                return executor.getPassengers()
                    .stream()
                    .map(passenger -> {
                        final SourceStack copy = stack.copy();
                        copy.write(passenger);
                        return copy;
                    })
                    .toList();
            });
        }

        public @NotNull Execute vehicle() {
            return redirect(stack -> {
                final Entity executor = stack.getExecutor();
                if (executor == null) return;
                final Entity vehicle = executor.getVehicle();
                if (vehicle == null) return;
                stack.write(vehicle);
            });
        }

        public @NotNull Execute owner() {
            return fork(stack -> {
                final Entity executor = stack.getExecutor();
                if (executor == null) return List.of();
                if (!(executor instanceof Tameable tameable)) return List.of();
                final AnimalTamer tamer = tameable.getOwner();
                if (tamer == null) return List.of();
                final Entity tamerEntity = stack.getDimension().getEntity(tamer.getUniqueId());
                if (tamerEntity == null) return List.of();
                stack.write(tamerEntity);
                return List.of(stack);
            });
        }

        public @NotNull Execute origin() {
            return fork(stack -> {
                final Entity executor = stack.getExecutor();
                if (executor == null) return List.of();

                return switch (executor) {
                    case Projectile projectile -> {
                        final UUID id = projectile.getOwnerUniqueId();
                        if (id == null) yield List.of();
                        final Entity owner = stack.getDimension().getEntity(id);
                        if (owner == null) yield List.of();
                        stack.write(owner);
                        yield List.of(stack);
                    }
                    case Item item -> {
                        final UUID id = item.getThrower();
                        if (id == null) yield List.of();
                        final Entity owner = stack.getDimension().getEntity(id);
                        if (owner == null) yield List.of();
                        stack.write(owner);
                        yield List.of(stack);
                    }
                    case EvokerFangs evokerFangs -> {
                        final Entity owner = evokerFangs.getOwner();
                        if (owner == null) yield List.of();
                        stack.write(owner);
                        yield List.of(stack);
                    }
                    case Vex vex -> {
                        final Entity owner = vex.getSummoner();
                        if (owner == null) yield List.of();
                        stack.write(owner);
                        yield List.of(stack);
                    }
                    case TNTPrimed tnt -> {
                        final Entity source = tnt.getSource();
                        if (source == null) yield List.of();
                        stack.write(source);
                        yield List.of(stack);
                    }
                    case AreaEffectCloud cloud -> {
                        final UUID id = cloud.getOwnerUniqueId();
                        if (id == null) yield List.of();
                        final Entity owner = stack.getDimension().getEntity(id);
                        if (owner == null) yield List.of();
                        stack.write(owner);
                        yield List.of(stack);
                    }
                    default -> List.of();
                };
            });
        }
    }

    public final class Summon {
        private Summon() {}

        public @NotNull Execute $(@NotNull EntityType entityType) {
            return redirect(stack -> stack.getDimension().spawnEntity(stack.getLocation().withWorld(stack.getDimension()), entityType));
        }
    }

    public final class Run {
        private Run() {}

        public void command(@NotNull String command) {
            redirect(stack -> {
                if (stack.getExecutor() == null) {
                    Bukkit.getServer().dispatchCommand(
                        Bukkit.getConsoleSender(),
                        String.format(
                            "execute in %s positioned %s rotated %s run %s",
                            DimensionProvider.get(stack.getDimension()).getId(),
                            stack.getLocation().format("$c $c $c"),
                            stack.getRotation().format("$c $c"),
                            command
                        )
                    );
                }
                else {
                    Bukkit.getServer().dispatchCommand(
                        stack.getExecutor(),
                        String.format(
                            "execute as %s in %s positioned %s rotated %s run %s",
                            stack.getExecutor().getUniqueId(),
                            DimensionProvider.get(stack.getDimension()).getId(),
                            stack.getLocation().format("$c $c $c"),
                            stack.getRotation().format("$c $c"),
                            command
                        )
                    );
                }
            });
        }
    }

    public void $(@NotNull Consumer<SourceStack> callback) {
        redirect(callback);
    }
}
