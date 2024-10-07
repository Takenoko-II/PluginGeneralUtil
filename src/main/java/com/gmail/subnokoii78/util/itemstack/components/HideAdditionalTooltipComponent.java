package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class HideAdditionalTooltipComponent extends ItemStackComponent {
    private HideAdditionalTooltipComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        });
    }

    public void enable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:hide_additional_tooltip";
    }
}
