package com.gmail.subnokoii78.util.event;

import org.bukkit.entity.Player;

public class PlayerClickEvent implements CustomEvent {
    private final Player player;

    protected PlayerClickEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
