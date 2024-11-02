package com.gmail.subnokoii78.util.vector;

import com.gmail.subnokoii78.util.other.TupleT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BoundedPlane {
    private final Vector3Builder center;

    private final TripleAxisRotationBuilder rotation;

    private final double width;

    private final double height;

    public BoundedPlane(@NotNull Vector3Builder center, @NotNull TripleAxisRotationBuilder rotation, double width, double height) {
        this.center = center.copy();
        this.rotation = rotation.copy();
        this.width = width;
        this.height = height;
    }

    public @NotNull Vector3Builder getCenter() {
        return center.copy();
    }

    public @NotNull TripleAxisRotationBuilder getRotation() {
        return rotation.copy();
    }

    public @NotNull Vector3Builder getNormal() {
        return rotation.getDirection3d();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public @Nullable Vector3Builder rayCast(@NotNull Vector3Builder from, @NotNull Vector3Builder to) {
        final Vector3Builder v1 = from.copy().subtract(center);
        final Vector3Builder v2 = to.copy().subtract(center);
        final Vector3Builder n = getNormal();

        if (v1.dot(n) * v2.dot(n) > 0) return null;

        final var provider = rotation.getLocalAxisProviderE();

        final Vector3Builder vx = provider.getX().length(width / 2);
        final Vector3Builder vy = provider.getY().length(height / 2);

        final Vector3Builder $00 = center.copy().subtract(vx).subtract(vy);
        final Vector3Builder $10 = center.copy().add(vx).subtract(vy);
        final Vector3Builder $01 = center.copy().subtract(vx).add(vy);
        final Vector3Builder $11 = center.copy().add(vx).add(vy);

        final List<TupleT<Vector3Builder>> beginAndEdgeVectorList = List.of(
            new TupleT<>($00, $01.copy().subtract($00)),
            new TupleT<>($01, $11.copy().subtract($01)),
            new TupleT<>($11, $10.copy().subtract($11)),
            new TupleT<>($10, $00.copy().subtract($10))
        );

        final Vector3Builder vA = from.copy().subtract($00);
        final Vector3Builder vB = to.copy().subtract($00);
        final double d1 = getDistanceBetween(from);
        final double d2 = getDistanceBetween(to);
        final double a = d1 / (d1 + d2);
        final Vector3Builder vC = vA.scale(1 - a).add(vB.scale(a));
        final Vector3Builder intersection = $00.copy().add(vC);

        final List<Vector3Builder> normals = new ArrayList<>();

        for (final TupleT<Vector3Builder> beginAndEdgeVector : beginAndEdgeVectorList) {
            final Vector3Builder begin = beginAndEdgeVector.left();
            final Vector3Builder v = beginAndEdgeVector.right();
            final Vector3Builder p = intersection.copy().subtract(begin);
            normals.add(p.cross(v).normalize());
        }

        for (Vector3Builder normal : normals) {
            if (!normal.similar(n, 8)) {
                return null;
            }
        }

        return intersection;
    }

    public double getDistanceBetween(@NotNull Vector3Builder point) {
        final Vector3Builder n = getNormal();
        return Math.abs(point.copy().subtract(center).dot(n));
    }
}
