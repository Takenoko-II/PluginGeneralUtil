package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DyedColorComponent extends TooltipShowable {
    private DyedColorComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return hasColor();
    }

    public @Nullable Color getColor() {
        return itemMetaDataSupplier(LeatherArmorMeta.class, LeatherArmorMeta::getColor);
    }

    public boolean hasColor() {
        return itemMetaDataSupplier(LeatherArmorMeta.class, leatherArmorMeta -> {
            return leatherArmorMeta.getColor().equals(DyedColorComponent.getDefaultLeatherColor());
        }, false);
    }

    public void setColor(@NotNull Color color) {
        itemMetaModifier(LeatherArmorMeta.class, leatherArmorMeta -> {
            leatherArmorMeta.setColor(color);
        });
    }

    @Override
    public void disable() {
        itemMetaModifier(LeatherArmorMeta.class, leatherArmorMeta -> {
            leatherArmorMeta.setColor(null);
        });
    }

    @Override
    public @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_DYE;
    }

    public static Color getDefaultLeatherColor() {
        final ItemMeta leatherHelmetMeta = new ItemStack(Material.LEATHER_HELMET).getItemMeta();

        if (leatherHelmetMeta instanceof LeatherArmorMeta) {
            return ((LeatherArmorMeta) leatherHelmetMeta).getColor();
        }
        else throw new RuntimeException("このエラー出るってことはこの世界がおかしい(？)");
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:dyed_color";
    }
}
