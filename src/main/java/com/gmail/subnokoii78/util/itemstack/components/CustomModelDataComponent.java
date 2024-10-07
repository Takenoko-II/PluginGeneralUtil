package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomModelDataComponent extends ItemStackComponent {
    private CustomModelDataComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasCustomModelData();
    }

    public @Nullable Integer getData() {
        if (itemStack.getItemMeta().hasCustomModelData()) {
            return itemStack.getItemMeta().getCustomModelData();
        }
        else return null;
    }

    public void setCustomModelData(int data) {
        itemMetaModifier(itemMeta -> {
            itemMeta.setCustomModelData(data);
        });
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setCustomModelData(null);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:custom_model_data";
    }
}
