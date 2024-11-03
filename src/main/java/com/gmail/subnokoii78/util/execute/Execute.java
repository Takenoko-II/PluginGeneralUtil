package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import io.papermc.paper.entity.Leashable;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Execute APIの心臓部
 * <br>executeコマンドのようにプラグインの処理を記述するためのクラス
 */
public class Execute {
    protected final List<SourceStack> stacks = new ArrayList<>();

    protected @NotNull Execute redirect(Consumer<SourceStack> modifier) {
        stacks.forEach(modifier);
        return this;
    }

    protected @NotNull Execute fork(Function<SourceStack, List<SourceStack>> modifier) {
        final List<SourceStack> newStacks = new ArrayList<>();
        for (final SourceStack stack : stacks) {
            newStacks.addAll(modifier.apply(stack.copy()));
        }
        stacks.clear();
        stacks.addAll(newStacks);
        return this;
    }

    /**
     * デフォルトのソーススタックを指定して{@link Execute}オブジェクトを生成します。
     * @param stack デフォルトの単一実行文脈
     */
    public Execute(@NotNull SourceStack stack) {
        this.stacks.add(stack);
    }

    /**
     * 空のソーススタックをデフォルトのソーススタックとして{@link Execute}オブジェクトを生成します。
     */
    public Execute() {
        this.stacks.add(new SourceStack());
    }

    protected static abstract class SubCommand<T extends Execute> {
        protected final T execute;

        protected SubCommand(@NotNull T execute) {
            this.execute = execute;
        }
    }

    /**
     * サブコマンドas
     * @param selector 実行者となるエンティティのセレクター
     * @return this
     */
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

    /**
     * サブコマンドas
     * @param selector 実行者となるエンティティのセレクター
     * @return this
     */
    public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector.Builder<T> selector) {
        return as(selector.build());
    }

    /**
     * サブコマンドat
     * @param selector 実行者となるエンティティのセレクター
     * @return this
     */
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

    /**
     * サブコマンドat
     * @param selector 実行者となるエンティティのセレクター
     * @return this
     */
    public <T extends Entity> @NotNull Execute at(@NotNull EntitySelector.Builder<T> selector) {
        return at(selector.build());
    }

    /**
     * サブコマンドpositioned
     */
    public final Positioned positioned = new Positioned(this);

    public static final class Positioned extends SubCommand<Execute> {
        private Positioned(@NotNull Execute execute) {
            super(execute);
        }

        /**
         * 絶対座標・相対座標・ローカル座標の入力によって実行座標を変更します。
         * @param input 座標の入力
         * @return that
         */
        public @NotNull Execute $(@NotNull String input) {
            return execute.redirect(stack -> {
                stack.write(stack.readCoordinates(input));
                stack.write(EntityAnchorType.FEET);
            });
        }

        /**
         * 参照するエンティティのセレクターの入力によって実行座標を変更します。
         * @param selector 参照するエンティティ
         * @return that
         */
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

        /**
         * 参照するエンティティのセレクターの入力によって実行座標を変更します。
         * @param selector 参照するエンティティ
         * @return that
         */
        public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector.Builder<T> selector) {
            return as(selector.build());
        }

        /**
         * サブサブコマンドover
         * @param heightMap Y座標の基準
         * @return that
         */
        public @NotNull Execute over(@NotNull HeightMap heightMap) {
            return execute.redirect(stack -> {
                final Location location = stack.getLocation().toHighestLocation(heightMap);
                stack.write(Vector3Builder.from(location));
                stack.write(DualAxisRotationBuilder.from(location));
                stack.write(location.getWorld());
            });
        }
    }

    /**
     * サブコマンドrotated
     */
    public final Rotated rotated = new Rotated(this);

    public static final class Rotated extends SubCommand<Execute> {
        private Rotated(@NotNull Execute execute) {
            super(execute);
        }

        /**
         * 絶対回転・相対回転の入力によって実行方向を変更します。
         * @param input 回転の入力
         * @return that
         */
        public @NotNull Execute $(@NotNull String input) {
            return execute.redirect(stack -> stack.write(stack.readAngles(input)));
        }

        /**
         * 参照するエンティティのセレクターの入力によって実行方向を変更します。
         * @param selector 参照するエンティティ
         * @return that
         */
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

        /**
         * 参照するエンティティのセレクターの入力によって実行方向を変更します。
         * @param selector 参照するエンティティ
         * @return that
         */
        public <T extends Entity> @NotNull Execute as(@NotNull EntitySelector.Builder<T> selector) {
            return as(selector.build());
        }
    }

    /**
     * サブコマンドfacing
     */
    public final Facing facing = new Facing(this);

    public static final class Facing extends SubCommand<Execute> {
        private Facing(@NotNull Execute execute) {
            super(execute);
        }

        /**
         * 絶対座標・相対座標・ローカル座標の入力によって実行方向を変更します。
         * @param input 座標の入力
         * @return that
         */
        public @NotNull Execute $(@NotNull String input) {
            return execute.redirect(stack -> {
                final Vector3Builder direction = stack.getPosition().add(stack.getEntityAnchor().getOffset()).getDirectionTo(stack.readCoordinates(input));
                stack.write(direction.getRotation2d());
            });
        }

        /**
         * 参照するエンティティのセレクターの入力によって実行方向を変更します。
         * @param selector 参照するエンティティ
         * @return that
         */
        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector<T> selector, @NotNull EntityAnchorType anchor) {
            return execute.fork(stack -> stack.getEntities(selector)
                .stream()
                .map(entity -> {
                    final SourceStack copy = stack.copy();
                    final Vector3Builder direction = copy.getPosition()
                        .add(copy.getEntityAnchor().getOffset())
                        .getDirectionTo(
                            Vector3Builder.from(entity)
                                .add(anchor.provideOffset(entity))
                        );
                    copy.write(direction.getRotation2d());
                    return copy;
                })
                .toList()
            );
        }

        /**
         * 参照するエンティティのセレクターの入力によって実行方向を変更します。
         * @param selector 参照するエンティティ
         * @return that
         */
        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector.Builder<T> selector, @NotNull EntityAnchorType anchor) {
            return entity(selector.build(), anchor);
        }
    }

    /**
     * サブコマンドalign
     * @param axes 切り捨てる軸
     * @return this
     */
    public @NotNull Execute align(@NotNull String axes) {
        return redirect(stack -> {
            final Set<Character> axisChars = SourceStack.readAxes(axes);
            final Vector3Builder location = stack.getPosition();

            if (axisChars.contains('x')) location.x(Math.floor(location.x()));
            if (axisChars.contains('y')) location.y(Math.floor(location.y()));
            if (axisChars.contains('z')) location.z(Math.floor(location.z()));

            stack.write(location);
        });
    }

    /**
     * サブコマンドanchored
     * @param anchor 実行アンカー
     * @return this
     */
    public @NotNull Execute anchored(@NotNull EntityAnchorType anchor) {
        return redirect(stack -> stack.write(anchor));
    }

    /**
     * サブコマンドin
     * @param dimension ディメンション
     * @return this
     */
    public @NotNull Execute in(@NotNull VanillaDimensionProvider dimension) {
        return redirect(stack -> stack.write(dimension.getWorld()));
    }

    /**
     * ガードサブコマンドif|unless
     * @param toggle ifまたはunless
     * @return ifまたはunless
     */
    public @NotNull GuardSubCommand ifOrUnless(@NotNull IfUnless toggle) {
        return new GuardSubCommand(this, toggle);
    }

    public static final class GuardSubCommand extends SubCommand<Execute> {
        private final IfUnless toggle;

        private GuardSubCommand(@NotNull Execute execute, @NotNull IfUnless toggle) {
            super(execute);
            this.toggle = toggle;
        }

        /**
         * 特定のエンティティが存在するかどうかをテストします。
         * @param selector セレクター
         * @return that
         */
        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector<T> selector) {
            return execute.fork(stack -> {
                if (toggle.apply(stack.getEntities(selector).isEmpty())) return List.of();
                else return List.of(stack);
            });
        }

        /**
         * 特定のエンティティが存在するかどうかをテストします。
         * @param selector セレクター
         * @return that
         */
        public <T extends Entity> @NotNull Execute entity(@NotNull EntitySelector.Builder<T> selector) {
            return entity(selector.build());
        }

        /**
         * 特定の座標に条件を満たすブロックが存在するかどうかをテストします。
         * @param location 座標の入力
         * @param blockPredicate ブロックの条件
         * @return that
         */
        public @NotNull Execute block(@NotNull String location, @NotNull Predicate<Block> blockPredicate) {
            return execute.fork(stack -> {
                final Block block = stack.getDimension().getBlockAt(
                    stack.readCoordinates(location)
                        .withWorld(stack.getDimension())
                );

                if (toggle.apply(blockPredicate.test(block))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        /**
         * 特定の2範囲のブロックが一致するかどうかをテストします。
         * @param begin 始点座標
         * @param end 種店座標
         * @param destination 比較先座標
         * @param scanMode 比較時のオプション
         * @return that
         */
        public @NotNull Execute blocks(@NotNull String begin, @NotNull String end, @NotNull String destination, @NotNull ScanMode scanMode) {
            return execute.fork(stack -> {
                final String command = String.format(
                    "execute if blocks %s %s %s %s",
                    begin, end, destination, scanMode.getId()
                );

                if (toggle.apply(stack.runCommand(command))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        /**
         * スコアボードの値が条件に一致するかどうかをテストします。
         * @param holder1 スコアホルダー1つ目
         * @param objectiveId1 オブジェクト1つ目
         * @param comparator 比較演算子
         * @param holder2 スコアホルダー2つ目
         * @param objectiveId2 オブジェクト2つ目
         * @return that
         */
        public @NotNull Execute score(@NotNull ScoreHolder holder1, @NotNull String objectiveId1, @NotNull ScoreComparator comparator, @NotNull ScoreHolder holder2, @NotNull String objectiveId2) {
            return execute.fork(stack -> {
                final Integer val1 = holder1.getScore(objectiveId1, stack);
                final Integer val2 = holder2.getScore(objectiveId2, stack);

                if (val1 == null || val2 == null) {
                    return List.of();
                }

                if (toggle.apply(comparator.compare(val1, val2))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        /**
         * スコアボードの値が条件に一致するかどうかをテストします。
         * @param holder スコアホルダー
         * @param objectiveId オブジェクト
         * @param range 数値の範囲
         * @return that
         */
        public @NotNull Execute score(@NotNull ScoreHolder holder, @NotNull String objectiveId, @NotNull ScoreRange range) {
            return execute.fork(stack -> {
                final Integer val = holder.getScore(objectiveId, stack);

                if (val == null) {
                    return List.of();
                }

                if (toggle.apply(range.min() <= val && val <= range.max())) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        /**
         * ディメンションが指定のものであるかどうかをテストします。
         * @param dimensionProvider ディメンション
         * @return that
         */
        public @NotNull Execute dimension(@NotNull VanillaDimensionProvider dimensionProvider) {
            return execute.fork(stack -> {
                if (toggle.apply(stack.getDimension().equals(dimensionProvider.getWorld()))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        /**
         * 特定の座標がロードされているかをテストします。
         * @param input 座標の入力
         * @return that
         */
        public @NotNull Execute loaded(@NotNull String input) {
            return execute.fork(stack -> {
                final Location location = stack.readCoordinates(input).withWorld(stack.getDimension());
                final Chunk chunk = stack.getDimension().getChunkAt(location);

                if (toggle.apply(chunk.isLoaded())) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        /**
         * 特定の座標におけるバイオームが指定のものであるかどうかをテストします。
         * @param input 座標の入力
         * @param value バイオーム
         * @return that
         */
        public @NotNull Execute biome(@NotNull String input, @NotNull Biome value) {
            return execute.fork(stack -> {
                final Location location = stack.readCoordinates(input).withWorld(stack.getDimension());
                final Biome biome = stack.getDimension().getBiome(location);

                if (toggle.apply(biome.equals(value))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }

        /**
         * サブサブコマンドitems
         */
        public final Items items = new Items(this);

        public static final class Items {
            private final GuardSubCommand ifUnless;

            private Items(@NotNull GuardSubCommand ifUnless) {
                this.ifUnless = ifUnless;
            }

            /**
             * 単一のエンティティの特定のスロット群にあるアイテムの中に条件を満たすアイテムがあるかどうかをテストします。
             * @param selector 単一のエンティティを示すセレクター
             * @param itemSlots アイテムスロットの候補
             * @param predicate 条件
             * @return that
             * @apiNote 適切なセレクターを渡さなければ例外が発生する可能性があります。
             */
            @ApiStatus.Experimental
            public <T> @NotNull Execute entity(@NotNull EntitySelector<? extends Entity> selector, @NotNull ItemSlotsGroup.ItemSlotsMatcher<T, ?> itemSlots, @NotNull Predicate<ItemStack> predicate) {
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

            /**
             * 単一のエンティティの特定のスロット群にあるアイテムの中に条件を満たすアイテムがあるかどうかをテストします。
             * @param selector 単一のエンティティを示すセレクター
             * @param itemSlots アイテムスロットの候補
             * @param predicate 条件
             * @return that
             */
            public <T> @NotNull Execute entity(@NotNull EntitySelector.Builder<? extends Entity> selector, @NotNull ItemSlotsGroup.ItemSlotsMatcher<T, ?> itemSlots, @NotNull Predicate<ItemStack> predicate) {
                return entity(selector.build(), itemSlots, predicate);
            }

            /**
             * あるブロックの特定のスロット群にあるアイテムの中に条件を満たすアイテムがあるかどうかをテストします。
             * @param input 座標の入力
             * @param itemSlots アイテムスロットの候補
             * @param predicate 条件
             * @return that
             */
            public @NotNull Execute block(@NotNull String input, @NotNull ItemSlotsGroup.ItemSlotsMatcher<InventoryHolder, ?> itemSlots, @NotNull Predicate<ItemStack> predicate) {
                return ifUnless.execute.fork(stack -> {
                    final BlockState blockState = stack.getDimension()
                        .getBlockAt(stack.readCoordinates(input).withWorld(stack.getDimension()))
                        .getState();

                    if (!(blockState instanceof BlockInventoryHolder blockInventoryHolder)) {
                        return List.of();
                    }

                    if (ifUnless.toggle.apply(itemSlots.matches(blockInventoryHolder, predicate))) {
                        return List.of(stack);
                    }
                    else {
                        return List.of();
                    }
                });
            }
        }

        /**
         * 指定の条件を満たすかどうかをテストします。
         * @param predicate 条件
         * @return that
         */
        public @NotNull Execute predicate(@NotNull Predicate<SourceStack> predicate) {
            return execute.fork(stack -> {
                final SourceStack copy = stack.copy();

                if (toggle.apply(predicate.test(copy))) {
                    return List.of(stack);
                }
                else return List.of();
            });
        }
    }

    /**
     * サブコマンドon
     */
    public final On on = new On(this);

    public static final class On extends SubCommand<Execute> {
        private On(@NotNull Execute execute) {
            super(execute);
        }

        /**
         * 実行者に騎乗しているエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute passengers() {
            return execute.fork(stack -> {
                if (!stack.hasExecutor()) return List.of();
                final Entity executor = stack.getExecutor();

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

        /**
         * 実行者が乗っている乗り物となるエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute vehicle() {
            return execute.redirect(stack -> {
                if (!stack.hasExecutor()) return;
                final Entity executor = stack.getExecutor();
                final Entity vehicle = executor.getVehicle();
                if (vehicle == null) return;
                stack.write(vehicle);
            });
        }

        /**
         * 実行者を飼いならしているエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute owner() {
            return execute.fork(stack -> {
                final Entity executor = stack.getExecutor();
                if (!stack.hasExecutor()) return List.of();
                if (!(executor instanceof Tameable tameable)) return List.of();
                final AnimalTamer tamer = tameable.getOwner();
                if (tamer == null) return List.of();
                final Entity tamerEntity = stack.getDimension().getEntity(tamer.getUniqueId());
                if (tamerEntity == null) return List.of();
                stack.write(tamerEntity);
                return List.of(stack);
            });
        }

        /**
         * 実行者の発生元となるエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute origin() {
            return execute.fork(stack -> {
                if (!stack.hasExecutor()) return List.of();

                return switch (stack.getExecutor()) {
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

        /**
         * 実行者が現在敵対しているエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute target() {
            return execute.fork(stack -> {
                if (!stack.hasExecutor()) return List.of();
                else if (!(stack.getExecutor() instanceof Mob mob)) return List.of();
                else if (mob.getTarget() == null) return List.of();
                else {
                    stack.write(mob.getTarget());
                    return List.of(stack);
                }
            });
        }

        /**
         * 実行者をリードで引っ張っているエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute leasher() {
            return execute.fork(stack -> {
                if (!stack.hasExecutor()) return List.of();
                else if (!(stack.getExecutor() instanceof Leashable leashable)) return List.of();
                else if (!leashable.isLeashed()) return List.of();
                else {
                    stack.write(leashable.getLeashHolder());
                    return List.of(stack);
                }
            });
        }

        private @NotNull Entity getExecuteOnEntity(@NotNull Entity executor, @NotNull String fromAfterOnToBeforeRun) {
            final String id = UUID.randomUUID().toString();
            final String command = String.format("execute on %s run tag @s add %s", fromAfterOnToBeforeRun, id);
            Bukkit.getServer().dispatchCommand(executor, command);
            return Bukkit.getServer()
                .getWorlds().stream()
                .flatMap(world -> world
                    .getEntities().stream()
                    .filter(entity -> {
                        if (entity.getScoreboardTags().contains(id)) {
                            entity.removeScoreboardTag(id);
                            return true;
                        }
                        else return false;
                    })
                )
                .toList().getFirst();
        }

        /**
         * 実行者を操縦しているエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute controller() {
            return execute.fork(stack -> {
                if (!stack.hasExecutor()) return List.of();
                else {
                    final Entity entity = getExecuteOnEntity(stack.getExecutor(), "controller");
                    stack.write(entity);
                    return List.of(stack);
                }
            });
        }

        /**
         * 実行者を直近5秒以内に攻撃したエンティティに実行者を渡します。
         * @return that
         */
        public @NotNull Execute attacker() {
            return execute.fork(stack -> {
                if (!stack.hasExecutor()) return List.of();
                else {
                    final Entity entity = getExecuteOnEntity(stack.getExecutor(), "attacker");
                    stack.write(entity);
                    return List.of(stack);
                }
            });
        }
    }

    /**
     * サブコマンドsummon
     * @param entityType 召喚するエンティティの種類
     * @return this
     */
    public @NotNull Execute summon(@NotNull EntityType entityType) {
        return redirect(stack -> {
            final Entity entity = stack.getDimension().spawnEntity(
                stack.getLocation(LocationGetter.DIMENSION, LocationGetter.POSITION),
                entityType
            );
            stack.write(entity);
        });
    }

    /**
     * サブコマンドrun
     */
    public final Run run = new Run(this);

    public static final class Run extends SubCommand<Execute> {
        private final Set<BiConsumer<SourceStack, Throwable>> catchers = new HashSet<>();

        private Run(@NotNull Execute execute) {
            super(execute);
        }

        /**
         * 現在の実行文脈を使用して指定のコマンドを実行します。
         * @param command コマンド文字列
         * @return 成功した場合true、失敗した場合false
         * @apiNote {@link org.bukkit.Server#dispatchCommand(CommandSender, String)}が整数ではなく真偽値を返してしまうため、{@link ResultCallback}に正しい値が渡されない問題があります。使用する意味も薄いため、将来的に削除される可能性があります。
         */
        @ApiStatus.Obsolete
        public boolean command(@NotNull String command) {
            return callback(stack -> stack.runCommand(command) ? SUCCESS : FAILURE);
        }

        /**
         * 現在の実行文脈を使用して渡された関数を実行します。
         * @param callback コールバック
         * @return 成功した場合true、失敗した場合false
         */
        public boolean callback(@NotNull Function<SourceStack, Integer> callback) {
            final AtomicBoolean success = new AtomicBoolean(false);

            execute.stacks.forEach(stack -> {
                try {
                    int result = callback.apply(stack.copy());
                    if (result > FAILURE) {
                        stack.getCallback().onSuccess(result);
                        success.set(true);
                    }
                    else {
                        stack.getCallback().onFailure();
                    }
                }
                catch (Throwable e) {
                    stack.getCallback().onFailure();
                    catchers.forEach(catcher -> catcher.accept(stack.copy(), e));
                }
            });

            return success.get();
        }

        /**
         * エラーを原因に実行が失敗したときに呼び出されるコールバックを登録します。
         * <br>この関数は{@link Execute.Run#callback(Function)}が発生した例外を握り潰してしまうため用意されています。
         * @param catcher コールバック
         * @return this
         */
        public @NotNull Run onCatch(@NotNull BiConsumer<SourceStack, Throwable> catcher) {
            catchers.add(catcher);
            return this;
        }
    }

    /**
     * サブコマンドstore
     */
    public final Store store = new Store(this);

    public static final class Store extends SubCommand<Execute> {
        private Store(@NotNull Execute execute) {
            super(execute);
        }

        /**
         * サブサブコマンドresult
         */
        public final DestinationProvider result = new DestinationProvider(this, StoreTarget.RESULT);

        /**
         * サブサブコマンドsuccess
         */
        public final DestinationProvider success = new DestinationProvider(this, StoreTarget.SUCCESS);

        private @NotNull Execute register(@NotNull StoreTarget target, @NotNull ResultConsumer resultConsumer) {
            return execute.redirect(stack -> stack.write(target, resultConsumer));
        }

        public static final class DestinationProvider {
            private final Store store;

            private final StoreTarget target;

            private DestinationProvider(@NotNull Store store, @NotNull StoreTarget target) {
                this.store = store;
                this.target = target;
            }

            /**
             * 実行によって得られた整数をエンティティに関連付けて格納します。
             * @param selector セレクター
             * @param consumer 格納する関数
             * @return that.that
             */
            public @NotNull Execute entity(@NotNull EntitySelector<? extends Entity> selector, @NotNull BiConsumer<Entity, Integer> consumer) {
                return store.register(target, (stack, integer) -> {
                    stack.getEntities(selector).forEach(entity -> consumer.accept(entity, integer));
                });
            }

            /**
             * 実行によって得られた整数をブロックに関連付けて格納します。
             * @param input 座標
             * @param consumer 格納する関数
             * @return that.that
             */
            public @NotNull Execute block(@NotNull String input, @NotNull BiConsumer<Block, Integer> consumer) {
                return store.register(target, (stack, integer) -> {
                    final Location location = stack.readCoordinates(input).withWorld(stack.getDimension());
                    final Block block = stack.getDimension().getBlockAt(location);
                    consumer.accept(block, integer);
                });
            }

            /**
             * 実行によって得られた整数をスコアボードに格納します。
             * @param scoreHolder スコアホルダー
             * @param objectiveId 格納するオブジェクト
             * @return that.that
             */
            public @NotNull Execute score(@NotNull ScoreHolder scoreHolder, @NotNull String objectiveId) {
                return store.register(target, (stack, integer) -> {
                    scoreHolder.setScore(objectiveId, stack, integer);
                });
            }

            /**
             * 実行によって得られた整数を使用できるコールバックを登録します。
             * @param resultConsumer コールバック
             * @return that.that
             */
            public @NotNull Execute consume(@NotNull ResultConsumer resultConsumer) {
                return store.register(target, resultConsumer);
            }
        }
    }

    public static final int SUCCESS = 1;

    public static final int FAILURE = 0;
}
