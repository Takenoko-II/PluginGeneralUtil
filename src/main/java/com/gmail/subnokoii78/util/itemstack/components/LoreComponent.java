package com.gmail.subnokoii78.util.itemstack.components;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class LoreComponent extends ItemStackComponent {
    private LoreComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return itemStack.getItemMeta().hasLore();
    }

    @Override
    public void disable() {
        itemMetaModifier(itemMeta -> {
            itemMeta.lore(null);
        });
    }

    public @NotNull List<? extends Component> getLore() {
        final List<Component> lore = itemStack.getItemMeta().lore();
        return lore == null ? List.of() : lore;
    }

    public void setLore(@NotNull List<? extends Component> lore) {
        itemMetaModifier(itemMeta -> {
            itemMeta.lore(lore);
        });
    }

    public void addLore(int index, @NotNull Component component) {
        final List<Component> components = new ArrayList<>(getLore());
        components.add(index, component);
        setLore(components);
    }

    public void addLore(@NotNull Component component) {
        final List<Component> components = new ArrayList<>(getLore());
        components.add(component);
        setLore(components);
    }

    public void removeLore(int index) {
        final List<Component> components = new ArrayList<>(getLore());
        components.remove(index);
        setLore(components);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:lore";
    }
}
