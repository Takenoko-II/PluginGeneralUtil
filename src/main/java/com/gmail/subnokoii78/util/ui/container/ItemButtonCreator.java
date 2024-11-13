package com.gmail.subnokoii78.util.ui.container;

import com.gmail.subnokoii78.util.ui.container.ItemButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemButtonCreator extends ItemButton {
    public ItemButtonCreator() {
        super(Material.BARRIER);
    }

    public abstract @NotNull ItemButton create(@NotNull Player player);

    @Override
    protected @NotNull ItemStack build() throws IllegalStateException {
        throw new IllegalStateException("create()が返すオブジェクトのbuild()を使用してください");
    }
}
