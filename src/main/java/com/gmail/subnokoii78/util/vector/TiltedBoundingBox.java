package com.gmail.subnokoii78.util.vector;

import com.gmail.subnokoii78.util.execute.EntitySelector;
import com.gmail.subnokoii78.util.execute.Execute;
import com.gmail.subnokoii78.util.execute.IfUnless;
import com.gmail.subnokoii78.util.execute.SelectorArgument;
import com.gmail.subnokoii78.util.other.TupleLR;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

/**
 * オイラー角を用いて自由に傾けることができる当たり判定のボックス
 */
public final class TiltedBoundingBox {
    private double width;

    private double height;

    private double depth;

    private final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder();

    private World world = Bukkit.getWorlds().getFirst();

    private final Vector3Builder center = new Vector3Builder();

    /**
     * 大きさを指定してボックスを作成します。
     * @param width 横の長さ
     * @param height 縦の長さ
     * @param depth 奥行きの長さ
     */
    public TiltedBoundingBox(double width, double height, double depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    /**
     * 1^3の立方体のボックスを作成します。
     */
    public TiltedBoundingBox() {
        this(1d, 1d, 1d);
    }

    /**
     * {@link Location}を使用してディメンション、座標、回転を一度に変更します。
     * <br>回転のロール角は0に設定されます。
     * <br>回転も変更するため、エンティティの位置に設置する目的で使用しないでください。
     * @param location Bukkit API の {@link Location}
     * @return this
     */
    public @NotNull TiltedBoundingBox put(@NotNull Location location) {
        return dimension(location.getWorld())
        .center(Vector3Builder.from(location))
        .rotation(TripleAxisRotationBuilder.from(DualAxisRotationBuilder.from(location)));
    }

    /**
     * ボックスの設置されているディメンションを取得します。
     * @return ディメンション
     */
    public @NotNull World dimension() {
        return world;
    }

    /**
     * ボックスのディメンションを変更します。
     * @param dimension ディメンション
     * @return this
     */
    public @NotNull TiltedBoundingBox dimension(@NotNull World dimension) {
        world = dimension;
        return this;
    }

    /**
     * ボックスの中心座標を取得します。
     * @return 中心座標
     */
    public @NotNull Vector3Builder center() {
        return center.copy();
    }

    /**
     * ボックスの中心座標を移動させます。
     * @param center 中心座標
     * @return this
     */
    public @NotNull TiltedBoundingBox center(@NotNull Vector3Builder center) {
        this.center
            .x(center.x())
            .y(center.y())
            .z(center.z());
        return this;
    }

    /**
     * ボックスの回転を取得します。
     * @return 回転
     */
    public @NotNull TripleAxisRotationBuilder rotation() {
        return rotation.copy();
    }

    /**
     * ボックスの回転を上書きします。
     * @param rotation 回転
     * @return this
     */
    public @NotNull TiltedBoundingBox rotation(@NotNull TripleAxisRotationBuilder rotation) {
        this.rotation
            .yaw(rotation.yaw())
            .pitch(rotation.pitch())
            .roll(rotation.roll());
        return this;
    }

    /**
     * ボックスの大きさを取得します。
     * @return 大きさ
     */
    public @NotNull Vector3Builder size() {
        return new Vector3Builder(width, height, depth);
    }

    /**
     * ボックスの大きさを変更します。
     * @param size 大きさ
     * @return this
     */
    public @NotNull TiltedBoundingBox size(@NotNull Vector3Builder size) {
        width = size.x();
        height = size.y();
        depth = size.z();
        return this;
    }

    /**
     * {@link Location}として諸々の情報を一度に取得します。
     * @return {@link Location}
     */
    public @NotNull Location getAsBukkitLocation() {
        return new Location(world, center.x(), center.y(), center.z(), rotation.yaw(), rotation.pitch());
    }

    /**
     * 座標がボックス内部にあるかどうかをテストします。
     * @param point　座標
     * @return 内部ならtrue
     */
    public boolean isInside(@NotNull Vector3Builder point) {
        final TripleAxisRotationBuilder.LocalAxisProviderE providerE = rotation.getLocalAxisProviderE();
        final Vector3Builder x = providerE.getX().length(width / 2);
        final Vector3Builder y = providerE.getY().length(height / 2);
        final Vector3Builder z = providerE.getZ().length(depth / 2);

        final Set<Vector3Builder> locations = Set.of(
            center().add(z), // forward
            center().subtract(z), // back
            center().add(x), // right
            center().subtract(x), // left
            center().add(y), // up
            center().subtract(y) // down
        );

        for (final Vector3Builder location : locations) {
            final Vector3Builder directionToCenter = location.getDirectionTo(center);
            final Vector3Builder directionToPoint = location.getDirectionTo(point);

            if (directionToCenter.dot(directionToPoint) < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * ボックスの8つの頂点座標をすべて取得します。
     * @return 頂点の絶対座標の {@link Set}
     */
    public @NotNull Set<Vector3Builder> getCornerPoints() {
        final TripleAxisRotationBuilder.LocalAxisProviderE providerE = rotation.getLocalAxisProviderE();
        final Vector3Builder x = providerE.getX().length(width / 2);
        final Vector3Builder y = providerE.getY().length(height / 2);
        final Vector3Builder z = providerE.getZ().length(depth / 2);

        return Set.of(
            center().subtract(x).subtract(y).subtract(z),
            center().add(x).subtract(y).subtract(z),
            center().subtract(x).add(y).subtract(z),
            center().subtract(x).subtract(y).add(z),
            center().add(x).add(y).subtract(z),
            center().subtract(x).add(y).add(z),
            center().add(x).subtract(y).add(z),
            center().add(x).add(y).add(z)
        );
    }

    /**
     * このボックスが他のボックスに衝突しているかどうかをテストします。
     * @param other 他のボックス
     * @return 衝突していればtrue
     */
    public boolean isCollides(@NotNull TiltedBoundingBox other) {
        if (!world.equals(other.world)) return false;

        // Separating Axis Theorem (SAT: 分離軸定理)

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
        final Set<Vector3Builder> cornersA = getCornerPoints();
        // Bの頂点座標
        final Set<Vector3Builder> cornersB = other.getCornerPoints();

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

    /**
     * このボックスが特定のエンティティに衝突しているかどうかをテストします。
     * @param entity エンティティ
     * @return 衝突していればtrue
     */
    public boolean isCollides(@NotNull Entity entity) {
        return isCollides(TiltedBoundingBox.of(entity));
    }

    /**
     * 6つの面を取得します。
     * @return {@link Set}<{@link BoundedPlane}>
     */
    @ApiStatus.Experimental
    public @NotNull Set<BoundedPlane> getBoundedPlanes() {
        final TripleAxisRotationBuilder.LocalAxisProviderE provider = rotation.getLocalAxisProviderE();
        final Vector3Builder x = provider.getX().length(width / 2);
        final Vector3Builder y = provider.getY().length(height / 2);
        final Vector3Builder z = provider.getZ().length(depth / 2);

        final Vector3Builder left = center().add(x);
        final Vector3Builder right = center().subtract(x);
        final Vector3Builder up = center().add(y);
        final Vector3Builder down = center().subtract(y);
        final Vector3Builder forward = center().add(z);
        final Vector3Builder back = center().subtract(z);

        // TripleAxisRotationBuilder::left()とか実装したい
        // やる気になるまでの仮実装ということで...
        final BoundedPlane leftPlane = new BoundedPlane(left, z.copy().invert(), y, x, depth, height);
        final BoundedPlane rightPlane = new BoundedPlane(right, z, y, x.copy().invert(), depth, height);
        final BoundedPlane upPlane = new BoundedPlane(up, x, z.copy().invert(), y, width, depth);
        final BoundedPlane downPlane = new BoundedPlane(down, x, z, y.copy().invert(), width, depth);
        final BoundedPlane forwardPlane = new BoundedPlane(forward, x, y, z, width, height);
        final BoundedPlane backPlane = new BoundedPlane(back, x.copy().invert(), y, z.copy().invert(), width, height);

        return Set.of(
            leftPlane, rightPlane,
            upPlane, downPlane,
            forwardPlane, backPlane
        );
    }

    /**
     * このボックスとのレイキャストを実行します。
     * @param from 線分の始点
     * @param to 線分の終点
     * @return 交差した場合ボックスとの最初の交点、交差していなければnull
     */
    public @Nullable Vector3Builder rayCast(@NotNull Vector3Builder from, @NotNull Vector3Builder to) {
        final List<Vector3Builder> intersections = new ArrayList<>();

        for (final BoundedPlane boundedPlane : getBoundedPlanes()) {
            final Vector3Builder intersection = boundedPlane.rayCast(from, to);
            if (intersection == null) continue;
            intersections.add(intersection);
        }

        if (intersections.isEmpty()) {
            return null;
        }

        return intersections.stream()
            .map(intersection -> {
                return new TupleLR<>(intersection, from.getDistanceTo(intersection));
            })
            .sorted((a, b) -> (int) (a.right() - b.right()))
            .toList()
            .getFirst().left();
    }

/*
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
*/

    /**
     * ボックスの外枠の座標それぞれにおいて関数を呼び出します。
     * @param callback コールバック
     */
    public void outline(@NotNull Consumer<Vector3Builder> callback) {
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
            callback.accept($000.lerp($100, i / 10f));
            callback.accept($000.lerp($010, i / 10f));
            callback.accept($000.lerp($001, i / 10f));
            callback.accept($101.lerp($100, i / 10f));
            callback.accept($101.lerp($111, i / 10f));
            callback.accept($101.lerp($001, i / 10f));
            callback.accept($110.lerp($010, i / 10f));
            callback.accept($110.lerp($100, i / 10f));
            callback.accept($110.lerp($111, i / 10f));
            callback.accept($011.lerp($010, i / 10f));
            callback.accept($011.lerp($001, i / 10f));
            callback.accept($011.lerp($111, i / 10f));
        }
    }

    /**
     * 範囲の外枠をdustパーティクルで描画します。
     * @param color パーティクルの色
     */
    public void showOutline(@NotNull Color color) {
        outline(v -> {
            world.spawnParticle(
                Particle.DUST,
                v.withWorld(world),
                1,
                0.0, 0.0, 0.0,
                0.01,
                new Particle.DustOptions(color, 0.5f)
            );
        });

        getCornerPoints().forEach(corner -> {
            world.spawnParticle(
                Particle.END_ROD,
                corner.withWorld(world),
                1,
                0.0, 0.0, 0.0,
                0.01
            );
        });
    }

    /**
     * 範囲の外枠を白色のdustパーティクルで描画します。
     */
    public void showOutline() {
        showOutline(Color.WHITE);
    }

    /**
     * このボックスに衝突しているエンティティをすべて取得します。
     * @return 衝突しているエンティティの {@link Set}
     */
    public @NotNull Set<Entity> getCollidingEntities() {
        return world.getEntities()
            .stream()
            .filter(this::isCollides)
            .collect(Collectors.toSet());
    }

    public static @NotNull TiltedBoundingBox of(@NotNull Entity entity) {
        final BoundingBox box = entity.getBoundingBox();
        return new TiltedBoundingBox()
            .size(new Vector3Builder(box.getWidthX(), box.getHeight(), box.getWidthZ()))
            .dimension(entity.getWorld())
            .center(Vector3Builder.from(box.getCenter()));
    }
}
