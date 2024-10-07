package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ShieldMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BaseColorComponent extends ItemStackComponent {
    private BaseColorComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        if (itemStack.getItemMeta() instanceof ShieldMeta shieldMeta) {
            return shieldMeta.getBaseColor() != null;
        }
        else return false;
    }

    @Override
    public void disable() {
        itemMetaModifier(ShieldMeta.class, shieldMeta -> {
            shieldMeta.setBaseColor(null);
        });
    }

    public @Nullable DyeColor getColor() throws IllegalStateException {
        return itemMetaDataSupplier(ShieldMeta.class, ShieldMeta::getBaseColor);
    }

    public void setColor(@NotNull DyeColor color) {
        itemMetaModifier(ShieldMeta.class, shieldMeta -> {
            shieldMeta.setBaseColor(color);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:base_color";
    }
}
