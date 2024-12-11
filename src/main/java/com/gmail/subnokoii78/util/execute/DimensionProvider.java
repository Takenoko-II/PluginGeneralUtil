package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DimensionProvider {
    private static final Map<NamespacedKey, DimensionProvider> INSTANCES = new HashMap<>();

    /**
     * オーバーワールド
     */
    public static final DimensionProvider OVERWORLD = new DimensionProvider(NamespacedKey.minecraft("overworld"));

    /**
     * ネザー
     */
    public static final DimensionProvider THE_NETHER = new DimensionProvider(NamespacedKey.minecraft("nether"));

    /**
     * ジ・エンド
     */
    public static final DimensionProvider THE_END = new DimensionProvider(NamespacedKey.minecraft("the_end"));

    private final NamespacedKey key;

    private DimensionProvider(@NotNull NamespacedKey key) {
        this.key = key;
        INSTANCES.put(key, this);
    }

    /**
     * ディメンションを取得します。
     * @return ディメンション
     * @throws IllegalStateException ディメンションが未生成か、バニラのディメンションではない場合
     */
    public @NotNull World getWorld() throws IllegalStateException {
        final World world = Bukkit.getWorld(key);

        if (world == null) {
            throw new IllegalStateException("ディメンションが見つかりませんでした: 未生成か、存在しない可能性があります");
        }

        return world;
    }

    /**
     * IDを取得します。
     * @return ID
     */
    public @NotNull String getId() {
        return key.toString();
    }

    public static @NotNull Set<DimensionProvider> values() {
        return Set.copyOf(INSTANCES.values());
    }

    /**
     * ディメンションから{@link DimensionProvider}を取得します。
     * @param key ディメンションID
     * @return 対応する {@link DimensionProvider}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull DimensionProvider of(@NotNull NamespacedKey key) throws IllegalArgumentException {
        if (INSTANCES.containsKey(key)) {
            return INSTANCES.get(key);
        }
        else if (Bukkit.getWorld(key) != null) {
            final DimensionProvider provider = new DimensionProvider(key);
            INSTANCES.put(key, provider);
            return provider;
        }
        else {
            throw new IllegalStateException("ディメンションが見つかりませんでした: 未生成か、存在しない可能性があります");
        }
    }

    /**
     * ディメンションから{@link DimensionProvider}を取得します。
     * @param world ディメンション
     * @return 対応する {@link DimensionProvider}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull DimensionProvider of(@NotNull World world) throws IllegalArgumentException {
        return of(world.getKey());
    }

    /**
     * IDから{@link DimensionProvider}を取得します。
     * @param id ディメンションID
     * @return 対応する {@link DimensionProvider}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull DimensionProvider of(@NotNull String id) throws IllegalArgumentException {
        final NamespacedKey key = NamespacedKey.fromString(id);

        if (key == null) {
            throw new IllegalArgumentException("無効なキーです");
        }

        return of(key);
    }
}
