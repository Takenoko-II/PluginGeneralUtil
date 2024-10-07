package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class RecipesComponent extends ItemStackComponent {
    private RecipesComponent(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean isEnabled() {
        return hasRecipes();
    }

    public boolean hasRecipes() {
        return itemMetaDataSupplier(KnowledgeBookMeta.class, KnowledgeBookMeta::hasRecipes, false);
    }

    public boolean hasRecipe(@NotNull NamespacedKey recipe) {
        return getRecipes().contains(recipe);
    }

    public @NotNull Set<NamespacedKey> getRecipes() {
        return itemMetaDataSupplier(KnowledgeBookMeta.class, knowledgeBookMeta -> {
            return Set.copyOf(knowledgeBookMeta.getRecipes());
        }, Set.of());
    }

    public void setRecipes(@NotNull Set<NamespacedKey> recipes) {
        itemMetaModifier(KnowledgeBookMeta.class, knowledgeBookMeta -> {
            knowledgeBookMeta.setRecipes(List.copyOf(recipes));
        });
    }

    public void addRecipe(@NotNull NamespacedKey recipe) {
        itemMetaModifier(KnowledgeBookMeta.class, knowledgeBookMeta -> {
            knowledgeBookMeta.addRecipe(recipe);
        });
    }

    public void removeRecipe(@NotNull NamespacedKey recipe) {
        itemMetaModifier(KnowledgeBookMeta.class, knowledgeBookMeta -> {
            final Set<NamespacedKey> recipes = getRecipes();
            recipes.remove(recipe);
            knowledgeBookMeta.setRecipes(List.copyOf(recipes));
        });
    }

    @Override
    public void disable() {
        setRecipes(Set.of());
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:recipes";
    }
}
