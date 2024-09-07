package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public enum DimensionProvider {
    OVERWORLD(World.Environment.NORMAL, "minecraft:overworld"),

    THE_NETHER(World.Environment.NETHER, "minecraft_the_nether"),

    THE_END(World.Environment.THE_END, "minecraft:the_end");

    private final World.Environment environment;

    private final String id;

    DimensionProvider(@NotNull World.Environment environment, @NotNull String id) {
        this.environment = environment;
        this.id = id;
    }

    public @NotNull World getWorld() throws IllegalStateException {
        for (final World world : Bukkit.getWorlds()) {
            if (world.getEnvironment().equals(environment)) {
                return world;
            }
        }

        throw new IllegalStateException("ディメンションが見つかりませんでした 生成されていない可能性があります");
    }

    public @NotNull String getId() {
        return id;
    }

    public static @NotNull DimensionProvider get(@NotNull World world) {
        for (final DimensionProvider provider : values()) {
            if (provider.environment.equals(world.getEnvironment())) {
                return provider;
            }
        }

        throw new IllegalStateException("ディメンションが見つかりませんでした カスタムディメンションですか？");
    }
}
