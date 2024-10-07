package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class CanBreakComponent extends TooltipShowable {
    private CanBreakComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
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
        return "minecraft:can_break";
    }

    @Override
    protected @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_DESTROYS;
    }
}
