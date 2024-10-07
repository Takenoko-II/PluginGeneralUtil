package com.gmail.subnokoii78.util.itemstack.components;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemNameComponent extends ItemStackComponent {
    private ItemNameComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasItemName();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.itemName(null);
        });
    }

    public @Nullable TextComponent getItemName() {
        if (isEnabled()) {
            return (TextComponent) itemStack.getItemMeta().itemName();
        }
        else return null;
    }

    public void setItemName(TextComponent component) {
        itemMetaModifier(itemMeta -> {
            itemMeta.itemName(component);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:item_name";
    }
}
