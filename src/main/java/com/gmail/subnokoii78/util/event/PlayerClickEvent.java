package com.gmail.subnokoii78.util.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerClickEvent extends CancellableCustomEvent {
    private final Player player;

    protected PlayerClickEvent(Player player, Cancellable event) {
        super(event);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
