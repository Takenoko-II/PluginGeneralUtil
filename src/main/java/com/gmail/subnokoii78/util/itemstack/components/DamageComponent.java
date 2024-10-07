package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public final class DamageComponent extends ItemStackComponent {
    private DamageComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemMetaDataSupplier(Damageable.class, Damageable::hasDamage, false);
    }

    @Override
    public void disable() {
        itemMetaModifier(Damageable.class, damageable -> {
            damageable.setDamage(0);
        });
    }

    public int getDamage() {
        return itemMetaDataSupplier(Damageable.class, damageable -> {
            if (isEnabled()) return damageable.getDamage();
            else return 0;
        }, 0);
    }

    public void setDamage(int damage) {
        itemMetaModifier(Damageable.class, damageable -> {
            damageable.setDamage(damage);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:damage";
    }
}
