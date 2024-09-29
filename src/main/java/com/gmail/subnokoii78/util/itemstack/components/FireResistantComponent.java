package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class FireResistantComponent extends ItemStackComponent {
    private FireResistantComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.isFireResistant();
    }

    @Override
    public void disable() {
        itemMeta.setFireResistant(false);
    }

    public void enable() {
        itemMeta.setFireResistant(true);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:fire_resistant";
    }
}
