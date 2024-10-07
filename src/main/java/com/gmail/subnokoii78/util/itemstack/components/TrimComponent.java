package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TrimComponent extends TooltipShowable {
    private TrimComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return hasTrim();
    }

    public @Nullable ArmorTrim getTrim() {
        return itemMetaDataSupplier(ArmorMeta.class, ArmorMeta::getTrim);
    }

    public boolean hasTrim() {
        return itemMetaDataSupplier(ArmorMeta.class, ArmorMeta::hasTrim, false);
    }

    public void setTrim(@NotNull ArmorTrim trim) {
        itemMetaModifier(ArmorMeta.class, armorMeta -> {
            armorMeta.setTrim(trim);
        });
    }

    @Override
    public void disable() {
        itemMetaModifier(ArmorMeta.class, armorMeta -> {
            armorMeta.setTrim(null);
        });
    }

    @Override
    public @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_ARMOR_TRIM;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:trim";
    }
}
