package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProfileComponent extends ItemStackComponent {
    private ProfileComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return hasOwner();
    }

    public @Nullable OfflinePlayer getOwner() {
        return itemMetaDataSupplier(SkullMeta.class, SkullMeta::getOwningPlayer);
    }

    public boolean hasOwner() {
        return itemMetaDataSupplier(SkullMeta.class, SkullMeta::hasOwner, false);
    }

    public void setOwner(@NotNull OfflinePlayer player) {
        itemMetaModifier(SkullMeta.class, skullMeta -> {
            skullMeta.setOwningPlayer(player);
        });
    }

    @Override
    public void disable() {
        itemMetaModifier(SkullMeta.class, skullMeta -> {
            skullMeta.setOwningPlayer(null);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:profile";
    }
}
