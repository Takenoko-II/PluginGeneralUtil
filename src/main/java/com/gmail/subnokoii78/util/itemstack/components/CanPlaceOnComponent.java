package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class CanPlaceOnComponent extends TooltipShowable {
    private CanPlaceOnComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    public boolean isEnabled() {
        return false;
    }

    @Override
    public void disable() {
        throw new IllegalStateException("現在のPaperAPIでは実装不可能なため操作できません");
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:can_place_on";
    }

    @Override
    protected @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_PLACED_ON;
    }
}
