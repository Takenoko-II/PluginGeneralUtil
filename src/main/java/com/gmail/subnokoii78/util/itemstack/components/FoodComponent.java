package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.PotionContent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class FoodComponent extends ItemStackComponent {
    private FoodComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasFood();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.setFood(null);
        });
    }

    public int nutrition() {
        return itemStack.getItemMeta().getFood().getNutrition();
    }

    public FoodComponent nutrition(int value) {
        itemMetaModifier(itemMeta -> {
            itemMeta.getFood().setNutrition(value);
        });
        return this;
    }

    public float saturation() {
        return itemStack.getItemMeta().getFood().getSaturation();
    }

    public FoodComponent saturation(float value) {
        itemMetaModifier(itemMeta -> {
            itemMeta.getFood().setSaturation(value);
        });
        return this;
    }

    public boolean canAlwaysEat() {
        return itemStack.getItemMeta().getFood().canAlwaysEat();
    }

    public FoodComponent canAlwaysEat(boolean flag) {
        itemMetaModifier(itemMeta -> {
            itemMeta.getFood().setCanAlwaysEat(flag);
        });
        return this;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:food";
    }
}
