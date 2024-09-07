package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public enum DimensionProvider {
    OVERWORLD(World.Environment.NORMAL),

    THE_NETHER(World.Environment.NETHER),

    THE_END(World.Environment.THE_END);

    private final World.Environment environment;

    DimensionProvider(@NotNull World.Environment environment) {
        this.environment = environment;
    }

    public @NotNull World getWorld() throws IllegalStateException {
        for (final World world : Bukkit.getWorlds()) {
            if (world.getEnvironment().equals(environment)) {
                return world;
            }
        }

        throw new IllegalStateException("ディメンションが見つかりませんでした 生成されていない可能性があります");
    }
}
