package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class CanPlaceOnComponent extends TooltipShowable {
    private CanPlaceOnComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
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
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
    }

    @Override
    void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
        else itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
    }
}
