package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class FireResistantComponent extends ItemStackComponent {
    private FireResistantComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().isFireResistant();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setFireResistant(false);
        });
    }

    public void enable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setFireResistant(true);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:fire_resistant";
    }
}
