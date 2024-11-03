package com.gmail.subnokoii78.util.other;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class InstanceAccessor<T> {
    private final Class<T> clazz;

    protected InstanceAccessor(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public @NotNull T newInstance(Object... arguments) {
        try {
            final var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        }
        catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static final @NotNull InstanceAccessor<Void> VOID = new InstanceAccessor<>(Void.class);
}
