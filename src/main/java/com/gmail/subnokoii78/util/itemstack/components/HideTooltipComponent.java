package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class HideTooltipComponent extends ItemStackComponent {
    private HideTooltipComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.isHideTooltip();
    }

    @Override
    public void disable() {
        itemMeta.setHideTooltip(false);
    }

    public void enable() {
        itemMeta.setHideTooltip(true);
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:hide_tooltip";
    }
}
