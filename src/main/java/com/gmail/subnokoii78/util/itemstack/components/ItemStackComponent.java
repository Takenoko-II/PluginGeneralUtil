package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ItemStackComponent {
    /**
     * このコンポーネントを保持する{@link ItemStack}
     */
    protected final ItemStack itemStack;

    /**
     * {@link ItemStack}をもとにこのコンポーネントを操作するインスタンスを作成します。
     * @param itemStack このコンポーネントを保持する{@link ItemStack}
     */
    protected ItemStackComponent(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    protected void itemMetaModifier(@NotNull Consumer<ItemMeta> modifier) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        modifier.accept(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    protected <T extends ItemMeta> void itemMetaModifier(@NotNull Class<T> clazz, Consumer<T> modifier) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (clazz.isInstance(itemMeta)) {
            modifier.accept(clazz.cast(itemMeta));
            itemStack.setItemMeta(itemMeta);
        }
    }

    protected <T extends ItemMeta, U> @Nullable U itemMetaDataSupplier(@NotNull Class<T> clazz, Function<T, U> modifier) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (clazz.isInstance(itemMeta)) {
            return modifier.apply(clazz.cast(itemMeta));
        }
        else return null;
    }

    protected <T extends ItemMeta, U> @NotNull U itemMetaDataSupplier(@NotNull Class<T> clazz, Function<T, U> modifier, @NotNull U defaultValue) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (clazz.isInstance(itemMeta)) {
            return modifier.apply(clazz.cast(itemMeta));
        }
        else return defaultValue;
    }

    /**
     * このコンポーネントが有効になっているかどうかを調べます。
     * @return 有効であれば真
     */
    public abstract boolean isEnabled();

    /**
     * このコンポーネントを無効化します。
     */
    public abstract void disable();

    /**
     * このコンポーネントのIDを返します。
     * @return コンポーネントID
     */
    public abstract @NotNull String getComponentId();
}
