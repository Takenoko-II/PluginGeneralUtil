package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.file.json.JSONArray;
import com.gmail.subnokoii78.util.file.json.JSONSerializer;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        this.stacks.add(stack);
    }

    public Execute() {
        this.stacks.add(new SourceStack());
    }

    public static abstract class MultiFunctionalSubCommand {
        protected final Execute execute;

        private MultiFunctionalSubCommand(@NotNull Execute execute) {
            this.execute = execute;
        }
    }

    public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector<T> selector) {
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

    public <T extends Entity> @NotNull Execute at(@NotNull EntitySelector<T> selector) {
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

    public final Positioned positioned = new Positioned(this);

    public static final class Positioned extends MultiFunctionalSubCommand {
        private Positioned(@NotNull Execute execute) {
            super(execute);
        }

        public @NotNull Execute $(@NotNull String input) {
            return execute.redirect(stack -> {
                stack.write(stack.readCoordinates(input));
                stack.write(EntityAnchor.FEET);
            });
        }

        public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector<T> selector) {
            return execute.fork(stack -> stack.getEntities(selector)
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

    public final Rotated rotated = new Rotated(this);

    public static final class Rotated extends MultiFunctionalSubCommand {
        private Rotated(@NotNull Execute execute) {
            super(execute);
        }

        public @NotNull Execute $(@NotNull String input) {
            return execute.redirect(stack -> stack.write(stack.readAngles(input)));
        }

        public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector<T> selector) {
            return execute.fork(stack -> stack.getEntities(selector)
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

    public final Facing facing = new Facing(this);

    public static final class Facing extends MultiFunctionalSubCommand {
        private Facing(@NotNull Execute execute) {
            super(execute);
        }

        public @NotNull Execute $(@NotNull String input) {
            return execute.redirect(stack -> {
                final Vector3Builder direction = stack.getLocation().add(stack.getEntityAnchorOffset()).getDirectionTo(stack.readCoordinates(input));
                stack.write(direction.getRotation2d());
            });
        }

        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector<T> selector, @NotNull EntityAnchor anchor) {
            return execute.fork(stack -> stack.getEntities(selector)
                .stream()
                .map(entity -> {
                    final SourceStack copy = stack.copy();
                    final Vector3Builder direction = copy.getLocation()
                        .add(copy.getEntityAnchorOffset())
                        .getDirectionTo(
                            Vector3Builder.from(entity)
                                .add(anchor.getOffset(entity))
                        );
                    copy.write(direction.getRotation2d());
                    return copy;
                })
                .toList()
            );
        }
    }

    public @NotNull Execute align(@NotNull String axes) {
        return redirect(stack -> {
            final Set<Character> axisChars = stack.readAxes(axes);
            final Vector3Builder location = stack.getLocation();

            if (axisChars.contains('x')) location.x(Math.floor(location.x()));
            if (axisChars.contains('y')) location.y(Math.floor(location.y()));
            if (axisChars.contains('z')) location.z(Math.floor(location.z()));

            stack.write(location);
        });
    }

    public @NotNull Execute anchored(@NotNull EntityAnchor anchor) {
        return redirect(stack -> stack.write(anchor));
    }

    public @NotNull Execute in(@NotNull DimensionProvider dimension) {
        return redirect(stack -> stack.write(dimension.getWorld()));
    }

    public @NotNull GuardSubCommandIfUnless ifOrUnless(@NotNull IfUnless toggle) {
        return new GuardSubCommandIfUnless(this, toggle);
    }

    public static final class GuardSubCommandIfUnless extends MultiFunctionalSubCommand {
        private final IfUnless toggle;

        private GuardSubCommandIfUnless(@NotNull Execute execute, @NotNull IfUnless toggle) {
            super(execute);
            this.toggle = toggle;
        }

        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector<T> selector) {
            return execute.fork(stack -> {
                if (toggle.invertOrNot(stack.getEntities(selector).isEmpty())) return List.of();
                else return List.of(stack);
            });
        }

        public @NotNull Execute block(@NotNull String location, @NotNull Predicate<Block> blockPredicate) {
            return execute.fork(stack -> {
                final Block block = stack.getDimension().getBlockAt(
                    stack.readCoordinates(location)
                        .withWorld(stack.getDimension())
                );

                if (toggle.invertOrNot(blockPredicate.test(block))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute blocks(@NotNull String begin, @NotNull String end, @NotNull String destination, @NotNull ScanMode scanMode) {
            return execute.fork(stack -> {
                final String command = String.format(
                    "execute if blocks %s %s %s %s",
                    begin, end, destination, scanMode.getId()
                );

                if (toggle.invertOrNot(stack.runCommand(command))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute score(@NotNull ScoreHolder holder1, @NotNull String objectiveId1, @NotNull ScoreComparator comparator, @NotNull ScoreHolder holder2, @NotNull String objectiveId2) {
            return execute.fork(stack -> {
                final Integer val1 = holder1.getScore(objectiveId1, stack);
                final Integer val2 = holder2.getScore(objectiveId2, stack);

                if (val1 == null || val2 == null) {
                    return List.of();
                }

                if (toggle.invertOrNot(comparator.compare(val1, val2))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute score(@NotNull ScoreHolder holder, @NotNull String objectiveId, @NotNull IntRange range) {
            return execute.fork(stack -> {
                final Integer val = holder.getScore(objectiveId, stack);

                if (val == null) {
                    return List.of();
                }

                if (toggle.invertOrNot(range.min() <= val && val <= range.max())) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute dimension(@NotNull DimensionProvider dimensionProvider) {
            return execute.fork(stack -> {
                if (toggle.invertOrNot(stack.getDimension().equals(dimensionProvider.getWorld()))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute loaded(@NotNull String input) {
            return execute.fork(stack -> {
                final Location location = stack.readCoordinates(input).withWorld(stack.getDimension());
                final Chunk chunk = stack.getDimension().getChunkAt(location);

                if (toggle.invertOrNot(chunk.isLoaded())) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public @NotNull Execute biome(@NotNull String input, @NotNull Biome value) {
            return execute.fork(stack -> {
                final Location location = stack.readCoordinates(input).withWorld(stack.getDimension());
                final Biome biome = stack.getDimension().getBiome(location);

                if (toggle.invertOrNot(biome.equals(value))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        public final Items items = new Items(this);

        public static final class Items {
            private final GuardSubCommandIfUnless ifUnless;

            private Items(@NotNull GuardSubCommandIfUnless ifUnless) {
                this.ifUnless = ifUnless;
            }

            @ApiStatus.Experimental
            public <T> @NotNull Execute entity(@NotNull EntitySelector<? extends Entity> selector, @NotNull ItemSlotsGroup.ItemSlots<T, ?> itemSlots, @NotNull Predicate<ItemStack> predicate) {
                if (!selector.isSingle()) {
                    throw new IllegalArgumentException("セレクターは単一のエンティティを指定する必要があります");
                }

                return ifUnless.execute.fork(stack -> {
                    for (final Entity entity : stack.getEntities(selector)) {
                        if (itemSlots.matches((T) entity, predicate)) {
                            if (ifUnless.toggle.equals(IfUnless.IF)) {
                                return List.of(stack);
                            }
                            else return List.of();
                        }
                    }

                    if (ifUnless.toggle.equals(IfUnless.IF)) {
                        return List.of();
                    }
                    else return List.of(stack);
                });
            }

            public @NotNull Execute block(@NotNull String input, @NotNull ItemSlotsGroup.ItemSlots<InventoryHolder, ?> itemSlots, @NotNull Predicate<ItemStack> predicate) {
                return ifUnless.execute.fork(stack -> {
                    final BlockState blockState = stack.getDimension()
                        .getBlockAt(stack.readCoordinates(input).withWorld(stack.getDimension()))
                        .getState();

                    if (!(blockState instanceof BlockInventoryHolder blockInventoryHolder)) {
                        return List.of();
                    }

                    if (ifUnless.toggle.invertOrNot(itemSlots.matches(blockInventoryHolder, predicate))) {
                        return List.of(stack);
                    }
                    else {
                        return List.of();
                    }
                });
            }
        }

        public @NotNull Execute predicate(@NotNull Predicate<SourceStack> predicate) {
            return execute.fork(stack -> {
                final SourceStack copy = stack.copy();

                if (toggle.invertOrNot(predicate.test(copy))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }
    }

    public final On on = new On(this);

    public static final class On extends MultiFunctionalSubCommand {
        private On(@NotNull Execute execute) {
            super(execute);
        }

        public @NotNull Execute passengers() {
            return execute.fork(stack -> {
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
            return execute.redirect(stack -> {
                final Entity executor = stack.getExecutor();
                if (executor == null) return;
                final Entity vehicle = executor.getVehicle();
                if (vehicle == null) return;
                stack.write(vehicle);
            });
        }

        public @NotNull Execute owner() {
            return execute.fork(stack -> {
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
            return execute.fork(stack -> {
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

    public @NotNull Execute summon(@NotNull EntityType entityType) {
        return redirect(stack -> stack.getDimension().spawnEntity(stack.getLocation().withWorld(stack.getDimension()), entityType));
    }

    public final Run run = new Run(this);

    public static final class Run extends MultiFunctionalSubCommand {
        private Run(@NotNull Execute execute) {
            super(execute);
        }

        public boolean command(@NotNull String command) {
            final List<Boolean> out = new ArrayList<>();

            execute.stacks.forEach(stack -> out.add(stack.runCommand(command)));

            return out.contains(true);
        }

        public void callback(@NotNull Consumer<SourceStack> callback) {
            execute.stacks.forEach(stack -> callback.accept(stack.copy()));
        }
    }

    @Override
    public @NotNull String toString() {
        final JSONArray jsonArray = new JSONArray();
        stacks.forEach(stack -> {
            jsonArray.add(stack.getAsJSONObject());
        });
        return new JSONSerializer(jsonArray).serialize();
    }
}
