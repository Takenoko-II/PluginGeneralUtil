package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Advancements extends HashMap<Advancement, Boolean> {
    private Advancements() {
        super();
    }

    public static @NotNull Advancements of(@NotNull String... values) {
        final Advancements advancements = new Advancements();

        for (final String value : values) {
            final String[] separated = value.split("=");

            if (separated.length > 2) {
                throw new IllegalArgumentException("無効な形式です");
            }

            final String advancementId = separated[0].trim();

            if (advancementId.split(":").length > 2) {
                throw new IllegalArgumentException("無効な形式です");
            }

            final NamespacedKey advancementKey = NamespacedKey.fromString(advancementId);

            if (advancementKey == null) {
                throw new IllegalArgumentException("無効な形式です");
            }

            final Advancement advancement = Bukkit.getAdvancement(advancementKey);

            final String boolStr = separated[1].replaceAll("[\\s\\n]+", "").trim();

            final boolean flag;

            if (boolStr.equals("true")) flag = true;
            else if (boolStr.equals("false")) flag = false;
            else throw new IllegalArgumentException("無効な形式です");

            advancements.put(advancement, flag);
        }

        return advancements;
    }
}
