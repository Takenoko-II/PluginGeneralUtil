package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class MaxStackSizeComponent extends ItemStackComponent {
    private MaxStackSizeComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasMaxStackSize();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setMaxStackSize(null);
        });
    }

    public int getMaxStackSize() {
        return itemStack.getItemMeta().getMaxStackSize();
    }

    public void setMaxStackSize(int stackSize) {
        itemMetaModifier(itemMeta -> {
            itemMeta.setMaxStackSize(stackSize);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:max_stack_size";
    }
}
