package com.gmail.subnokoii78.util.itemstack.components;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class StoredEnchantmentsComponent extends TooltipShowable {
    private StoredEnchantmentsComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemMetaDataSupplier(EnchantmentStorageMeta.class, EnchantmentStorageMeta::hasStoredEnchants, false);
    }

    public @NotNull Map<Enchantment, Integer> getStoredEnchantments() {
        return itemMetaDataSupplier(EnchantmentStorageMeta.class, EnchantmentStorageMeta::getStoredEnchants, Map.of());
    }

    public void addStoredEnchantment(Enchantment enchantment, int level) {
        itemMetaModifier(EnchantmentStorageMeta.class, enchantmentStorageMeta -> {
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
        });
    }

    public void removeStoredEnchantment(Enchantment enchantment) {
        itemMetaModifier(EnchantmentStorageMeta.class, enchantmentStorageMeta -> {
            enchantmentStorageMeta.removeStoredEnchant(enchantment);
        });
    }

    @Override
    public void disable() {
        RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).forEach(this::removeStoredEnchantment);
    }

    @Override
    protected @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_STORED_ENCHANTS;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:stored_enchantments";
    }
}
