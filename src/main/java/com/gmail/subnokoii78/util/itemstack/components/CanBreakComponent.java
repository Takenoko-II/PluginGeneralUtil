package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class CanBreakComponent extends TooltipShowable {
    private CanBreakComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
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
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
    }

    @Override
    void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
        else itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
    }
}
