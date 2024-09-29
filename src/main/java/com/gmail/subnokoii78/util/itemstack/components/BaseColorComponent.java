package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.DyeColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.ShieldMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BaseColorComponent extends ItemStackComponent {
    private BaseColorComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof ShieldMeta shieldMeta) {
            return shieldMeta.getBaseColor() != null;
        }
        else return false;
    }

    @Override
    public void disable() {
        if (itemMeta instanceof ShieldMeta shieldMeta) {
            shieldMeta.setBaseColor(null);
        }
    }

    public @Nullable DyeColor getColor() throws IllegalStateException {
        if (itemMeta instanceof ShieldMeta shieldMeta) {
            return shieldMeta.getBaseColor();
        }
        else return null;
    }

    public void setColor(@NotNull DyeColor color) {
        if (itemMeta instanceof ShieldMeta shieldMeta) {
            shieldMeta.setBaseColor(color);
        }
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:base_color";
    }
}
