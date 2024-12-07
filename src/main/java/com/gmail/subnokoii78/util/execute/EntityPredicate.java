package com.gmail.subnokoii78.util.execute;

import org.bukkit.entity.Entity;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface EntityPredicate extends BiPredicate<Entity, SourceStack> {
}
