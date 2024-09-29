package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class EnchantableComponent extends ItemStackComponent {
    private EnchantableComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void disable() {
        throw new IllegalStateException("現在のPaperAPIでは実装不可能なため操作できません");
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:enchantable";
    }
}
