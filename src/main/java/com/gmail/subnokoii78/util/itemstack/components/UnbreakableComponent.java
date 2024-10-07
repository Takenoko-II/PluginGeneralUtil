package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class UnbreakableComponent extends TooltipShowable {
    private UnbreakableComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().isUnbreakable();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setUnbreakable(false);
        });
    }

    public void enable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setUnbreakable(true);
        });
    }

    @Override
    public @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_UNBREAKABLE;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:unbreakable";
    }
}
