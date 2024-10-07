package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.PotionContent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.FoodComponent.FoodEffect;
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

    public int eatTicks() {
        return (int) Math.floor(itemStack.getItemMeta().getFood().getEatSeconds() * 20);
    }

    public FoodComponent eatTicks(int ticks) {
        itemMetaModifier(itemMeta -> {
            itemMeta.getFood().setEatSeconds(((float) ticks) / 20f);
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

    public Map<PotionContent, Float> getEffects() {
        final Map<PotionContent, Float> map = new HashMap<>();

        itemStack.getItemMeta().getFood()
            .getEffects()
            .stream()
            .forEach(foodEffect -> {
                final PotionEffect effect = foodEffect.getEffect();
                map.put(
                    PotionContent.fromBukkit(effect),
                    foodEffect.getProbability()
                );
            });

        return map;
    }

    public FoodComponent addEffect(@NotNull PotionContent effect, float probability) {
        itemMetaModifier(itemMeta -> {
            itemMeta.getFood().addEffect(effect.toBukkit(), probability);
        });
        return this;
    }

    public FoodComponent removeEffect(@NotNull PotionEffectType type) {
        itemMetaModifier(itemMeta -> {
            final List<FoodEffect> effects = new ArrayList<>(itemMeta.getFood().getEffects());
            itemMeta.getFood().setEffects(
                effects.stream()
                    .filter(effect -> !effect.getEffect().getType().equals(type))
                    .toList()
            );
        });
        return this;
    }

    public FoodComponent setEffects(@NotNull Map<PotionContent, Float> map) {
        itemMetaModifier(itemMeta -> {
            map.keySet().forEach(content -> itemMeta.getFood().addEffect(content.toBukkit(), map.get(content)));
        });
        return this;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:food";
    }
}
