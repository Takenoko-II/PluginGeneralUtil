package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ChargedProjectilesComponent extends ItemStackComponent {
    private ChargedProjectilesComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof CrossbowMeta crossbowMeta) {
            return crossbowMeta.hasChargedProjectiles();
        }
        else return false;
    }

    @Override
    public void disable() {
        if (itemMeta instanceof CrossbowMeta crossbowMeta) {
            crossbowMeta.setChargedProjectiles(null);
        }
    }

    public @NotNull List<ItemStack> getChargedProjectiles() {
        if (itemMeta instanceof CrossbowMeta crossbowMeta) {
            return crossbowMeta.getChargedProjectiles();
        }
        else return List.of();
    }

    public void addChargedProjectiles(@NotNull ItemStack... itemStacks) {
        if (itemMeta instanceof CrossbowMeta crossbowMeta) {
            for (final ItemStack itemStack : itemStacks) {
                crossbowMeta.addChargedProjectile(itemStack);
            }
        }
    }

    public void setChargedProjectiles(@NotNull ItemStack... itemStacks) {
        if (itemMeta instanceof CrossbowMeta crossbowMeta) {
            crossbowMeta.setChargedProjectiles(List.of(itemStacks));
        }
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:charged_projectiles";
    }
}
