package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.TypedAttributeModifier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class AttributeModifiersComponent extends TooltipShowable {
    private AttributeModifiersComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasAttributeModifiers();
    }

    @Override
    public void disable() {
        for (final TypedAttributeModifier modifier : getModifiers()) {
            removeModifier(modifier);
        }
    }

    public TypedAttributeModifier[] getModifiers() {
        final Multimap<Attribute, AttributeModifier> map = itemStack.getItemMeta().getAttributeModifiers();

        if (map == null) {
            return new TypedAttributeModifier[0];
        }

        return TypedAttributeModifier.fromBukkit(map);
    }

    public void setModifiers(TypedAttributeModifier[] modifiers) {
        final Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();

        for (final TypedAttributeModifier modifier : modifiers) {
            map.put(modifier.getType(), modifier.toBukkit());
        }

        itemMetaModifier(itemMeta -> {
            itemMeta.setAttributeModifiers(map);
        });
    }

    public void addModifier(TypedAttributeModifier modifier) {
        itemMetaModifier(itemMeta -> {
            itemMeta.addAttributeModifier(modifier.getType(), modifier.toBukkit());
        });
    }

    public void removeModifier(TypedAttributeModifier modifier) {
        itemMetaModifier(itemMeta -> {
            itemMeta.removeAttributeModifier(modifier.getType(), modifier.toBukkit());
        });
    }

    public void removeModifiers(Attribute type) {
        itemMetaModifier(itemMeta -> {
            itemMeta.removeAttributeModifier(type);
        });
    }

    @Override
    protected @NotNull ItemFlag getItemFlag() {
        return ItemFlag.HIDE_ATTRIBUTES;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:attribute_modifiers";
    }
}
