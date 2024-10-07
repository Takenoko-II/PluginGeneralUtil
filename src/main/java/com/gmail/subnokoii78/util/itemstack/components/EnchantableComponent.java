package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class EnchantableComponent extends ItemStackComponent {
    private EnchantableComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
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
