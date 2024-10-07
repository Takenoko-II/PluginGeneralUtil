package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ToolComponent extends ItemStackComponent {
    private ToolComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasTool();
    }

    public int damagePerBlock() {
        return itemStack.getItemMeta().getTool().getDamagePerBlock();
    }

    public void damagePerBlock(int damage) {
        itemStack.getItemMeta().getTool().setDamagePerBlock(damage);
    }

    public float defaultMiningSpeed() {
        return itemStack.getItemMeta().getTool().getDefaultMiningSpeed();
    }

    public void defaultMiningSpeed(float speed) {
        itemStack.getItemMeta().getTool().setDefaultMiningSpeed(speed);
    }

    public @NotNull Set<org.bukkit.inventory.meta.components.ToolComponent.ToolRule> getRules() {
        return Set.copyOf(itemStack.getItemMeta().getTool().getRules());
    }

    public void addRule(Collection<Material> blocks, float speed, boolean isCorrectForDrops) {
        itemMetaModifier(itemMeta -> {
            itemMeta.getTool().addRule(blocks, speed, isCorrectForDrops);
        });
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setTool(null);
        });
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:tool";
    }
}
