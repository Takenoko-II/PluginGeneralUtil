package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class HideTooltipComponent extends ItemStackComponent {
    private HideTooltipComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().isHideTooltip();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setHideTooltip(false);
        });
    }

    public void enable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setHideTooltip(true);
        });
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:hide_tooltip";
    }
}
