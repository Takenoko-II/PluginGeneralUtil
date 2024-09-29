package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class HideAdditionalTooltipComponent extends ItemStackComponent {
    private HideAdditionalTooltipComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    @Override
    public void disable() {
        itemMeta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    public void enable() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:hide_additional_tooltip";
    }
}
