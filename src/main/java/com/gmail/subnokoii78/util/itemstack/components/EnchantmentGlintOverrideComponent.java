package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class EnchantmentGlintOverrideComponent extends ItemStackComponent {
    private EnchantmentGlintOverrideComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return getGlintOverride();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setEnchantmentGlintOverride(null);
        });
    }

    public boolean getGlintOverride() {
        return itemStack.getItemMeta().getEnchantmentGlintOverride();
    }

    public void setGlintOverride(boolean flag) {
        itemMetaModifier(itemMeta -> {
            itemMeta.setEnchantmentGlintOverride(flag);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:enchantment_glint_override";
    }
}
