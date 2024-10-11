package com.gmail.subnokoii78.util.event;

import org.jetbrains.annotations.NotNull;

public interface CustomEvent {
    @NotNull CustomEventType<?> getType();
}
