package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.PotionContent;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public final class PotionContentsComponent extends ItemStackComponent {
    private PotionContentsComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemMetaDataSupplier(PotionMeta.class, PotionMeta::hasCustomEffects, false);
    }

    public @NotNull Set<PotionContent> getContents() {
        return itemMetaDataSupplier(PotionMeta.class, potionMeta -> {
            if (isEnabled()) {
                return potionMeta.getCustomEffects()
                    .stream()
                    .map(PotionContent::fromBukkit)
                    .collect(Collectors.toSet());
            }
            else if (potionMeta.getBasePotionType() != null) {
                return potionMeta.getBasePotionType()
                    .getPotionEffects()
                    .stream()
                    .map(PotionContent::fromBukkit)
                    .collect(Collectors.toSet());
            }
            else return Set.of();
        }, Set.of());
    }

    public boolean hasContent(@NotNull PotionEffectType type) {
        return itemMetaDataSupplier(PotionMeta.class, potionMeta -> {
            return potionMeta.hasCustomEffect(type) || potionMeta.hasBasePotionType();
        }, false);
    }

    public void addContent(@NotNull PotionContent effect) {
        itemMetaModifier(PotionMeta.class, potionMeta -> {
            potionMeta.addCustomEffect(effect.toBukkit(), false);
        });
    }

    public @Nullable PotionType getBasePotion() {
        return itemMetaDataSupplier(PotionMeta.class, PotionMeta::getBasePotionType);
    }

    public void setBasePotion(@NotNull PotionType type) {
        itemMetaModifier(PotionMeta.class, potionMeta -> {
            potionMeta.setBasePotionType(type);
        });
    }

    public void removeContent(@NotNull PotionEffectType type) {
        itemMetaModifier(PotionMeta.class, potionMeta -> {
            potionMeta.removeCustomEffect(type);
        });
    }

    public @Nullable Color getColor() {
        return itemMetaDataSupplier(PotionMeta.class, PotionMeta::getColor);
    }

    public void setColor(@NotNull Color color) {
        itemMetaModifier(PotionMeta.class, potionMeta -> {
            potionMeta.setColor(color);
        });
    }

    @Override
    public void disable() {
        itemMetaModifier(PotionMeta.class, potionMeta -> {
            potionMeta.setBasePotionType(null);
            potionMeta.clearCustomEffects();
        });
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:potion_contents";
    }
}
