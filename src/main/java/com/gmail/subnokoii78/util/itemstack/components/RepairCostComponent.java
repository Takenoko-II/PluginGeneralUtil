package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RepairCostComponent extends ItemStackComponent {
    private RepairCostComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemMetaDataSupplier(Repairable.class, Repairable::hasRepairCost, false);
    }

    @Override
    public void disable() {
        itemMetaModifier(Repairable.class, repairable -> {
            repairable.setRepairCost(1);
        });
    }

    public @Nullable Integer getRepairCost() {
        return itemMetaDataSupplier(Repairable.class, Repairable::getRepairCost);
    }

    public void setRepairCost(int cost) {
        itemMetaModifier(Repairable.class, repairable -> {
            repairable.setRepairCost(cost);
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:repairable";
    }
}
