package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class EnchantmentsComponent extends TooltipShowable {
    private EnchantmentsComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasEnchants();
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return itemStack.getItemMeta().getEnchants();
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        itemMetaModifier(itemMeta -> {
            itemMeta.addEnchant(enchantment, level, true);
        });
    }

    public void removeEnchantment(Enchantment enchantment) {
        itemMetaModifier(itemMeta -> {
            itemMeta.removeEnchant(enchantment);
        });
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return itemStack.getItemMeta().hasEnchant(enchantment);
    }

    @Override
    public void disable() {
        itemMetaModifier(ItemMeta::removeEnchantments);
    }

    @Override
    protected @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_ENCHANTS;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:enchantments";
    }
}
