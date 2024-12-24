package com.gmail.subnokoii78.util.execute;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Advancements extends HashMap<Advancement, Boolean> {
    private Advancements() {
        super();
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        @RegExp
        private static final String PATTERN = "^[\\s\\n]*(?:[^\\s\\n=:]:[^\\s\\n=:]|[^\\s\\n=:])+[\\s\\n]*=[\\s\\n]*(?:true|false)[\\s\\n]*$";

        private final Advancements advancements = new Advancements();

        private Builder() {}

        public @NotNull Builder and(@NotNull @Pattern(value = PATTERN) String value) {
            final String[] separated = value.split("=");

            if (separated.length > 2) {
                throw new IllegalArgumentException("無効な形式です");
            }

            final String advancementId = separated[0].trim();

            if (advancementId.split(":").length > 2 || advancementId.contains(" ") || advancementId.contains("\n")) {
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

            return this;
        }

        public @NotNull Advancements build() {
            return advancements;
        }
    }
}
