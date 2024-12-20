package com.gmail.subnokoii78.util.ui.container;

import com.gmail.subnokoii78.util.itemstack.components.ComponentItemStackBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemButton {
    protected final ComponentItemStackBuilder itemStackBuilder;

    protected final UUID id = UUID.randomUUID();

    private final Set<Consumer<ItemButtonClickEvent>> listenerSet = new HashSet<>();

    private int amount = 1;

    public ItemButton(@NotNull Material material) {
        itemStackBuilder = new ComponentItemStackBuilder(material);
    }

    public @NotNull ItemButton name(@NotNull TextComponent component) {
        itemStackBuilder.itemName().setItemName(component);
        return this;
    }

    public @NotNull ItemButton addLore(@NotNull TextComponent component) {
        itemStackBuilder.lore().addLore(component);
        return this;
    }

    public @NotNull ItemButton setLore(@NotNull List<TextComponent> components) {
        itemStackBuilder.lore().setLore(components);
        return this;
    }

    public @NotNull ItemButton amount(int amount) {
        if (amount < 1 || amount > 127) {
            throw new IllegalArgumentException("個数としては範囲外の値です");
        }

        if (amount > 1) {
            itemStackBuilder.maxDamage().disable();
        }

        this.amount = amount;
        return this;
    }

    public @NotNull ItemButton glint(boolean flag) {
        itemStackBuilder.enchantmentGlintOverride().setGlintOverride(flag);
        return this;
    }

    public @NotNull ItemButton customModelData(int data) {
        itemStackBuilder.customModelData().setCustomModelData(data);
        return this;
    }

    public @NotNull ItemButton damage(float rate) throws IllegalStateException {
        if (amount > 1) {
            throw new IllegalStateException("耐久力の表示はアイテムの個数が1のときのみ利用できます");
        }
        itemStackBuilder.maxDamage().setMaxDamage(100);
        itemStackBuilder.damage().setDamage((int) rate * 100);
        return this;
    }

    public @NotNull ItemButton onClick(Consumer<ItemButtonClickEvent> listener) {
        listenerSet.add(listener);
        return this;
    }

    protected @NotNull ItemStack build() {
        itemStackBuilder.maxStackSize().setMaxStackSize(amount);

        return itemStackBuilder
            .toItemStackBuilder()
            .count(amount)
            .dataContainer("id", id.toString())
            .build();
    }

    protected void click(@NotNull ItemButtonClickEvent event) {
        listenerSet.forEach(listener -> listener.accept(event));
    }
}
