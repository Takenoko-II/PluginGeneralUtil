package com.gmail.subnokoii78.util.event;

import org.bukkit.event.Cancellable;

public class CancellableCustomEvent implements CustomEvent {
    private final Cancellable event;

    protected CancellableCustomEvent(Cancellable event) {
        this.event = event;
    }

    public void cancel() {
        event.setCancelled(true);
    }
}
