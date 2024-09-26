package com.gmail.subnokoii78.util.event;

import org.bukkit.event.Cancellable;

public abstract class CancellableCustomEvent implements CustomEvent {
    private final Cancellable event;

    protected CancellableCustomEvent(Cancellable event) {
        this.event = event;
    }

    public void cancel() {
        event.setCancelled(true);
    }

    @Override
    public abstract CustomEventType<?> getType();
}
