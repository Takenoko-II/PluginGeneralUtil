package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public final class MaxDamageComponent extends ItemStackComponent {
    private MaxDamageComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemMetaDataSupplier(Damageable.class, Damageable::hasMaxDamage, false);
    }

    @Override
    public void disable() {
        itemMetaModifier(Damageable.class, damageable -> {
            damageable.setMaxDamage(null);
        });
    }

    public int getMaxDamage() {
        return itemMetaDataSupplier(Damageable.class, Damageable::getMaxDamage, 0);
    }

    public void setMaxDamage(int damage) throws IllegalStateException {
        if (itemStack.getItemMeta().getMaxStackSize() > 1) {
            throw new IllegalStateException("アイテムの最大スタック数は1である必要があります");
        }

        itemMetaModifier(Damageable.class, damageable -> {
            damageable.setMaxDamage(damage);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:max_damage";
    }
}
