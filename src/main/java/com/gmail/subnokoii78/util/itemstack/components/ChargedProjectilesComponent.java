package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ChargedProjectilesComponent extends ItemStackComponent {
    private ChargedProjectilesComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemMetaDataSupplier(CrossbowMeta.class, CrossbowMeta::hasChargedProjectiles, false);
    }

    @Override
    public void disable() {
        itemMetaModifier(CrossbowMeta.class, crossbowMeta -> {
            crossbowMeta.setChargedProjectiles(null);
        });
    }

    public @NotNull List<ItemStack> getChargedProjectiles() {
        return itemMetaDataSupplier(CrossbowMeta.class, CrossbowMeta::getChargedProjectiles, List.of());
    }

    public void addChargedProjectiles(@NotNull ItemStack... itemStacks) {
        itemMetaModifier(CrossbowMeta.class, crossbowMeta -> {
            for (final ItemStack itemStack : itemStacks) {
                crossbowMeta.addChargedProjectile(itemStack);
            }
        });
    }

    public void setChargedProjectiles(@NotNull ItemStack... itemStacks) {
        itemMetaModifier(CrossbowMeta.class, crossbowMeta -> {
            crossbowMeta.setChargedProjectiles(List.of(itemStacks));
        });
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:charged_projectiles";
    }
}
