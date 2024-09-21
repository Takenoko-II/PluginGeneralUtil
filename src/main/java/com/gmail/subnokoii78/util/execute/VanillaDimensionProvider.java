package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public enum VanillaDimensionProvider {
    /**
     * オーバーワールド
     */
    OVERWORLD(World.Environment.NORMAL, "minecraft:overworld"),

    /**
     * ネザー
     */
    THE_NETHER(World.Environment.NETHER, "minecraft:the_nether"),

    /**
     * ジ・エンド
     */
    THE_END(World.Environment.THE_END, "minecraft:the_end");

    private final World.Environment environment;

    private final String id;

    VanillaDimensionProvider(@NotNull World.Environment environment, @NotNull String id) {
        this.environment = environment;
        this.id = id;
    }

    /**
     * ディメンションを取得します。
     * @return ディメンション
     * @throws IllegalStateException ディメンションが未生成か、バニラのディメンションではない場合
     */
    public @NotNull World getWorld() throws IllegalStateException {
        for (final World world : Bukkit.getWorlds()) {
            if (world.getEnvironment().equals(environment)) {
                return world;
            }
        }

        throw new IllegalStateException("ディメンションが見つかりませんでした 生成されていない可能性があります");
    }

    /**
     * IDを取得します。
     * @return ID
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * ディメンションから{@link VanillaDimensionProvider}を取得します。
     * @param world ディメンション
     * @return 対応する {@link VanillaDimensionProvider}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull VanillaDimensionProvider get(@NotNull World world) throws IllegalArgumentException {
        for (final VanillaDimensionProvider provider : values()) {
            if (provider.environment.equals(world.getEnvironment())) {
                return provider;
            }
        }

        throw new IllegalArgumentException("ディメンションが見つかりませんでした カスタムディメンションですか？");
    }

    /**
     * IDから{@link VanillaDimensionProvider}を取得します。
     * @param id ディメンションID
     * @return 対応する {@link VanillaDimensionProvider}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull VanillaDimensionProvider get(@NotNull String id) throws IllegalArgumentException {
        for (final VanillaDimensionProvider provider : values()) {
            if (provider.getId().equals(id)) {
                return provider;
            }
        }

        throw new IllegalArgumentException("ディメンションが見つかりませんでした");
    }
}
