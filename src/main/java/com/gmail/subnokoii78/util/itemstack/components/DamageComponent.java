package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class DamageComponent extends ItemStackComponent {
    private DamageComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof Damageable damageable) {
            return damageable.hasDamage();
        }
        else return false;
    }

    @Override
    public void disable() {
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(0);
        }
    }

    public int getDamage() {
        if (itemMeta instanceof Damageable damageable) {
            if (isEnabled()) return damageable.getDamage();
            else return 0;
        }
        else return 0;
    }

    public void setDamage(int damage) {
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        }
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:damage";
    }
}
