package com.gmail.subnokoii78.util.itemstack.components;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomNameComponent extends ItemStackComponent {
    private CustomNameComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasDisplayName();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.displayName(null);
        });
    }

    public @Nullable TextComponent getCustomName() {
        if (isEnabled()) {
            return (TextComponent) itemStack.getItemMeta().displayName();
        }
        else return null;
    }

    public void setCustomName(@NotNull TextComponent component) {
        itemMetaModifier(itemMeta -> {
            itemMeta.displayName(component);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:custom_name";
    }
}
