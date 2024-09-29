package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class MaxDamageComponent extends ItemStackComponent {
    private MaxDamageComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof Damageable damageable) {
            return damageable.hasMaxDamage();
        }
        else return false;
    }

    @Override
    public void disable() {
        if (itemMeta instanceof Damageable damageable) {
            damageable.setMaxDamage(null);
        }
    }

    public int getMaxDamage() {
        if (itemMeta instanceof Damageable damageable) {
            if (isEnabled()) return damageable.getMaxDamage();
            else return 0;
        }
        else return 0;
    }

    public void setMaxDamage(int damage) throws IllegalStateException {
        if (itemMeta.getMaxStackSize() > 1) {
            throw new IllegalStateException("アイテムの最大スタック数は1である必要があります");
        }

        if (itemMeta instanceof Damageable damageable) {
            damageable.setMaxDamage(damage);
        }
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:max_damage";
    }
}
