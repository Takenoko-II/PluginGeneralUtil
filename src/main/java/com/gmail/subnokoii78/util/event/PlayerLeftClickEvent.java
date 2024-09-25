package com.gmail.subnokoii78.util.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerLeftClickEvent extends PlayerClickEvent {
    protected PlayerLeftClickEvent(Player player, Cancellable event) {
        super(player, event);
    }
}
