package com.gmail.subnokoii78.util.shape;

import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public final class VectorPrinter {
    private final World dimension;

    private final Vector3Builder center;

    public VectorPrinter(@NotNull World dimension, @NotNull Vector3Builder center) {
        this.dimension = dimension;
        this.center = center;
    }

    public VectorPrinter(@NotNull Location location) {
        this.dimension = location.getWorld();
        this.center = Vector3Builder.from(location);
    }

    public void print(@NotNull Vector3Builder vector3, @NotNull Color color) {
        dimension.spawnParticle(
            Particle.FLAME,
            center.withWorld(dimension),
            1,
            0.0d, 0.0d, 0.0d,
            0.0d
        );

        dimension.spawnParticle(
            Particle.SOUL_FIRE_FLAME,
            center.copy().add(vector3).withWorld(dimension),
            1,
            0.0d, 0.0d, 0.0d,
            0.0d
        );

        new ShapeTemplate()
            .scale((float) vector3.length())
            .world(dimension)
            .center(center)
            .particle(new DustSpawner(new Particle.DustOptions(color, 0.5f)))
            .rotation(TripleAxisRotationBuilder.from(vector3.getRotation2d()))
            .newShape(StraightLine.class)
            .draw();
    }

    public void print(@NotNull DualAxisRotationBuilder rotation2, @NotNull Color color) {
        print(rotation2.getDirection3d(), color);
    }

    public void print(@NotNull TripleAxisRotationBuilder rotation3, @NotNull Color color) {
        print(rotation3.getDirection3d(), color);
    }
}
