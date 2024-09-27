package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ToolComponent extends ItemStackComponent {
    private ToolComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.hasTool();
    }

    public int damagePerBlock() {
        return itemMeta.getTool().getDamagePerBlock();
    }

    public void damagePerBlock(int damage) {
        itemMeta.getTool().setDamagePerBlock(damage);
    }

    public float defaultMiningSpeed() {
        return itemMeta.getTool().getDefaultMiningSpeed();
    }

    public void defaultMiningSpeed(float speed) {
        itemMeta.getTool().setDefaultMiningSpeed(speed);
    }

    public Set<org.bukkit.inventory.meta.components.ToolComponent.ToolRule> getRules() {
        return new HashSet<>(itemMeta.getTool().getRules());
    }

    public void addRule(Collection<Material> blocks, float speed, boolean isCorrectForDrops) {
        itemMeta.getTool().addRule(blocks, speed, isCorrectForDrops);
    }

    @Override
    public void disable() {
        itemMeta.setTool(null);
    }

    @NotNull
    @Override
    public String getComponentId() {
        return "minecraft:tool";
    }
}
