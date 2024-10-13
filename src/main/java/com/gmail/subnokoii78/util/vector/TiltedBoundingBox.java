package com.gmail.subnokoii78.util.vector;

import com.gmail.subnokoii78.util.shape.ParticleSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*
 *                       _oo0oo_
 *                      o8888888o
 *                      88" . "88
 *                      (| -_- |)
 *                      0\  =  /0
 *                    ___/`---'\___
 *                  .' \\|     |// '.
 *                 / \\|||  :  |||// \
 *                / _||||| -:- |||||- \
 *               |   | \\\  -  /// |   |
 *               | \_|  ''\---/''  |_/ |
 *               \  .-\__  '-'  ___/-. /
 *             ___'. .'  /--.--\  `. .'___
 *          ."" '<  `.___\_<|>_/___.' >' "".
 *         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *         \  \ `_.   \_ __\ /__ _/   .-` /  /
 *     =====`-.____`.___ \_____/___.-`___.-'=====
 *                       `=---='
 *
 *
 *
 *     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *               佛祖保佑         永无BUG
 */

public final class TiltedBoundingBox {
    private final double width;

    private final double height;

    private final double depth;

    private final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder();

    private World world = Bukkit.getWorlds().getFirst();

    private final Vector3Builder center = new Vector3Builder();

    public TiltedBoundingBox() {
        width = 1d;
        height = 1d;
        depth = 1d;
    }

    public TiltedBoundingBox(double width, double height, double depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void put(@NotNull World world, @NotNull Vector3Builder location) {
        this.world = world;
        center.x(location.x());
        center.y(location.y());
        center.z(location.z());
    }

    public void rotate(@NotNull TripleAxisRotationBuilder rotation) {
        this.rotation.add(rotation);
    }

    public boolean isInside(Vector3Builder vector3) {
        final TripleAxisRotationBuilder.LocalAxisProviderE axes = rotation.getLocalAxisProviderE();
        final Vector3Builder x = axes.getX().length(width / 2);
        final Vector3Builder y = axes.getY().length(height / 2);
        final Vector3Builder z = axes.getZ().length(depth / 2);

        final Vector3Builder[] locations = new Vector3Builder[]{
            center.copy().add(z), // forward
            center.copy().subtract(z), // back
            center.copy().add(x), // right
            center.copy().subtract(x), // left
            center.copy().add(y), // up
            center.copy().subtract(y) // down
        };

        for (final Vector3Builder location : locations) {
            final Vector3Builder directionToCenter = location.getDirectionTo(center);
            final Vector3Builder directionToPoint = location.getDirectionTo(vector3);

            if (directionToCenter.dot(directionToPoint) < 0) {
                return false;
            }
        }

        return true;
    }

    public @NotNull Set<Vector3Builder> getCorners() {
        final TripleAxisRotationBuilder.LocalAxisProviderE providerE = rotation.getLocalAxisProviderE();
        final Vector3Builder x = providerE.getX().length(width / 2);
        final Vector3Builder y = providerE.getY().length(height / 2);
        final Vector3Builder z = providerE.getZ().length(depth / 2);

        return Set.of(
            center.copy().subtract(x).subtract(y).subtract(z),
            center.copy().add(x).subtract(y).subtract(z),
            center.copy().subtract(x).add(y).subtract(z),
            center.copy().subtract(x).subtract(y).add(z),
            center.copy().add(x).add(y).subtract(z),
            center.copy().subtract(x).add(y).add(z),
            center.copy().add(x).subtract(y).add(z),
            center.copy().add(x).add(y).add(z)
        );
    }

    public boolean isCollides(@NotNull TiltedBoundingBox other) {
        if (!world.equals(other.world)) return false;

        final var providerA = rotation.getLocalAxisProviderE();
        final var providerB = other.rotation.getLocalAxisProviderE();

        // Aのそれぞれの面の法線ベクトル
        final Vector3Builder ax = providerA.getX();
        final Vector3Builder ay = providerA.getY();
        final Vector3Builder az = providerA.getZ();
        // Bのそれぞれの面の法線ベクトル
        final Vector3Builder bx = providerB.getX();
        final Vector3Builder by = providerB.getY();
        final Vector3Builder bz = providerB.getZ();

        // A, Bの法線ベクトル同士の外積を含む15の分離軸
        final Set<Vector3Builder> axes = Set.of(
            ax, ay, az,
            bx, by, bz,
            ax.cross(bx), ax.cross(by), ax.cross(bz),
            ay.cross(bx), ay.cross(by), ay.cross(bz),
            az.cross(bx), az.cross(by), az.cross(bz)
        );

        // Aの頂点座標
        final Set<Vector3Builder> cornersA = getCorners();
        // Bの頂点座標
        final Set<Vector3Builder> cornersB = other.getCorners();

        // あとで使うレコード
        record Range(double min, double max) {
            public boolean includes(double value) {
                return min <= value && value <= max;
            }
        }

        // それぞれの分離軸について
        for (final Vector3Builder axis : axes) {
            // 分離軸とAの頂点座標の内積たち
            final Set<Double> vA = new HashSet<>();
            // 分離軸とBの頂点座標の内積たち
            final Set<Double> vB = new HashSet<>();

            // vAにぶち込む
            for (final Vector3Builder cornerA : cornersA) {
                vA.add(axis.dot(cornerA));
            }

            // vBにぶち込む
            for (final Vector3Builder cornerB : cornersB) {
                vB.add(axis.dot(cornerB));
            }

            // vAの最小値と最大値
            final double minA = vA.stream().reduce(Double.POSITIVE_INFINITY, Math::min);
            final double maxA = vA.stream().reduce(Double.NEGATIVE_INFINITY, Math::max);

            // vBの最小値と最大値
            final double minB = vB.stream().reduce(Double.POSITIVE_INFINITY, Math::min);
            final double maxB = vB.stream().reduce(Double.NEGATIVE_INFINITY, Math::max);

            final Range rangeA = new Range(minA, maxA);
            final Range rangeB = new Range(minB, maxB);

            // Aの範囲内にBの値が存在する or Bの範囲内にAの値が存在する = その分離軸においてA, Bは衝突している
            if ((rangeA.includes(minB) || rangeA.includes(maxB)) || (rangeB.includes(minA) || rangeB.includes(maxA))) {
                // 次の軸も確かめる
                continue;
            }
            else {
                // 衝突していない！ -> false
                return false;
            }
        }

        // 全軸が衝突を返したとき
        return true;
    }

    public boolean isCollides(@NotNull Entity entity) {
        final BoundingBox box = entity.getBoundingBox();

        final TiltedBoundingBox instance = new TiltedBoundingBox(
            box.getWidthX(),
            box.getHeight(),
            box.getWidthZ()
        );

        instance.put(entity.getWorld(), Vector3Builder.from(box.getCenter()));

        return isCollides(instance);
    }

    @Deprecated
    private boolean getIsIntersectingBySeparatingAxisTheorem(BoundingBox box) {
        final TripleAxisRotationBuilder.LocalAxisProviderE axes = rotation.getLocalAxisProviderE();
        final Vector3Builder x = axes.getX().length(width / 2);
        final Vector3Builder y = axes.getY().length(height / 2);
        final Vector3Builder z = axes.getZ().length(depth / 2);

        final Vector3Builder normalAX = center.copy().subtract(x).getDirectionTo(center);
        final Vector3Builder normalAY = center.copy().subtract(y).getDirectionTo(center);
        final Vector3Builder normalAZ = center.copy().subtract(z).getDirectionTo(center);

        final Vector3Builder normalBX = new Vector3Builder(1, 0, 0);
        final Vector3Builder normalBY = new Vector3Builder(0, 1, 0);
        final Vector3Builder normalBZ = new Vector3Builder(0, 0, 1);

        final Vector3Builder crossAXBX = normalAX.cross(normalBX);
        final Vector3Builder crossAXBY = normalAX.cross(normalBY);
        final Vector3Builder crossAXBZ = normalAX.cross(normalBZ);

        final Vector3Builder crossAYBX = normalAY.cross(normalBX);
        final Vector3Builder crossAYBY = normalAY.cross(normalBY);
        final Vector3Builder crossAYBZ = normalAY.cross(normalBZ);

        final Vector3Builder crossAZBX = normalAZ.cross(normalBX);
        final Vector3Builder crossAZBY = normalAZ.cross(normalBY);
        final Vector3Builder crossAZBZ = normalAZ.cross(normalBZ);

        final Vector3Builder[] separators = new Vector3Builder[]{
            normalAX, normalAY, normalAZ,
            normalBX, normalBY, normalBZ,
            crossAXBX, crossAXBY, crossAXBZ,
            crossAYBX, crossAYBY, crossAYBZ,
            crossAZBX, crossAZBY, crossAZBZ
        };

        final Vector3Builder[] cornersA = new Vector3Builder[]{
            center.copy().subtract(x).subtract(y).subtract(z),
            center.copy().add(x).subtract(y).subtract(z),
            center.copy().subtract(x).add(y).subtract(z),
            center.copy().subtract(x).subtract(y).add(z),
            center.copy().add(x).add(y).subtract(z),
            center.copy().subtract(x).add(y).add(z),
            center.copy().add(x).subtract(y).add(z),
            center.copy().add(x).add(y).add(z)
        };

        final Vector3Builder[] cornersB = new Vector3Builder[]{
            Vector3Builder.from(box.getMin()),
            Vector3Builder.from(box.getMin()).add(new Vector3Builder(box.getWidthX(), 0, 0)),
            Vector3Builder.from(box.getMin()).add(new Vector3Builder(0, box.getHeight(), 0)),
            Vector3Builder.from(box.getMin()).add(new Vector3Builder(0, 0, box.getWidthZ())),
            Vector3Builder.from(box.getMin()).add(new Vector3Builder(box.getWidthX(), box.getHeight(), 0)),
            Vector3Builder.from(box.getMin()).add(new Vector3Builder(0, box.getHeight(), box.getWidthZ())),
            Vector3Builder.from(box.getMin()).add(new Vector3Builder(box.getWidthX(), 0, box.getWidthZ())),
            Vector3Builder.from(box.getMin()).add(new Vector3Builder(box.getWidthX(), box.getHeight(), box.getWidthZ()))
        };

        for (final Vector3Builder separator : separators) {
            final List<Vector3Builder> pointsA = new ArrayList<>();

            for (final Vector3Builder cornerA : cornersA) {
                pointsA.add(cornerA.projection(separator));
            }

            final Vector3Builder vm = separator.copy().invert().scale(100d).selectClosest(pointsA.toArray(Vector3Builder[]::new));
            final Vector3Builder vM = separator.copy().scale(100d).selectClosest(pointsA.toArray(Vector3Builder[]::new));

            for (final Vector3Builder cornerB : cornersB) {
                final Vector3Builder pointB = cornerB.projection(separator);

                // ここの条件式でvmとvM, vmとpointB, vMとpointBのどれかが完全一致してる
                if (vm.getDirectionTo(vM).dot(vm.getDirectionTo(pointB)) > 0 && vM.getDirectionTo(vm).dot(vM.getDirectionTo(pointB)) > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    @Deprecated
    private boolean isIntersectingBox(BoundingBox box, int rayCount) {
        final TripleAxisRotationBuilder.LocalAxisProviderE axes = rotation.getLocalAxisProviderE();
        final Vector3Builder x = axes.getX().length(width / 2);
        final Vector3Builder y = axes.getY().length(height / 2);
        final Vector3Builder z = axes.getZ().length(depth / 2);

        final Vector3Builder $000 = center.copy().subtract(x).subtract(y).subtract(z);
        final Vector3Builder forward = z.copy().length(depth);

        for (int i = 0; i < rayCount; i++) {
            final Vector3Builder start = $000.copy()
            .add(x.copy().length(Math.random() * width))
            .add(y.copy().length(Math.random() * height));

            final Vector3Builder end = start.copy().add(forward);

            final RayTraceResult result = box.rayTrace(start.toBukkitVector(), start.getDirectionTo(end).toBukkitVector(), start.getDistanceTo(end));

            if (result != null) return true;
        }

        return false;
    }

    private void outline(Consumer<Vector3Builder> consumer) {
        final TripleAxisRotationBuilder.LocalAxisProviderE axes = rotation.getLocalAxisProviderE();
        final Vector3Builder x = axes.getX().length(width / 2);
        final Vector3Builder y = axes.getY().length(height / 2);
        final Vector3Builder z = axes.getZ().length(depth / 2);

        final Vector3Builder $000 = center.copy().subtract(x).subtract(y).subtract(z);
        final Vector3Builder $100 = center.copy().add(x).subtract(y).subtract(z);
        final Vector3Builder $010 = center.copy().subtract(x).add(y).subtract(z);
        final Vector3Builder $001 = center.copy().subtract(x).subtract(y).add(z);
        final Vector3Builder $110 = center.copy().add(x).add(y).subtract(z);
        final Vector3Builder $011 = center.copy().subtract(x).add(y).add(z);
        final Vector3Builder $101 = center.copy().add(x).subtract(y).add(z);
        final Vector3Builder $111 = center.copy().add(x).add(y).add(z);

        for (int i = 0; i < 10; i++) {
            consumer.accept($000.lerp($100, i / 10f));
            consumer.accept($000.lerp($010, i / 10f));
            consumer.accept($000.lerp($001, i / 10f));
            consumer.accept($101.lerp($100, i / 10f));
            consumer.accept($101.lerp($111, i / 10f));
            consumer.accept($101.lerp($001, i / 10f));
            consumer.accept($110.lerp($010, i / 10f));
            consumer.accept($110.lerp($100, i / 10f));
            consumer.accept($110.lerp($111, i / 10f));
            consumer.accept($011.lerp($010, i / 10f));
            consumer.accept($011.lerp($001, i / 10f));
            consumer.accept($011.lerp($111, i / 10f));
        }

        consumer.accept(center.copy());
        consumer.accept(center.copy().add(x.length(3d)));
        consumer.accept(center.copy().add(y.length(3d)));
        consumer.accept(center.copy().add(z.length(3d)));
    }

    public void showOutline() {
        showOutline(Color.WHITE);
    }

    public void showOutline(@NotNull Color color) {
        outline(v -> world.spawnParticle(
            Particle.DUST,
            v.withWorld(world),
            1,
            0.0, 0.0, 0.0,
            0.01,
            new Particle.DustOptions(color, 0.5f)
        ));

        getCorners().forEach(corner -> {
            new ParticleSpawner<>(Particle.END_ROD, null)
                .place(world, corner)
                .speed(0)
                .count(1)
                .spawn();
        });
    }

    @Deprecated
    public Entity[] getIntersection() {
        return world.getEntities()
        .stream().filter(entity -> isIntersectingBox(entity.getBoundingBox(), 30))
        .toArray(Entity[]::new);
    }

    @Deprecated
    public Entity[] getIntersection(float densityOfRays) {
        final int rayCount = Math.min((int) Math.floor(densityOfRays * 100), 100);

        return world.getEntities()
        .stream().filter(entity -> isIntersectingBox(entity.getBoundingBox(), rayCount))
        .toArray(Entity[]::new);
    }

    @ApiStatus.Experimental
    public @NotNull Set<Entity> getIntersectingEntitiesBySAT() {
        return world.getEntities()
            .stream()
            .filter(this::isCollides)
            .collect(Collectors.toSet());
    }
}
