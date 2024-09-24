package com.gmail.subnokoii78.util.event;

import org.bukkit.entity.Player;

public class PlayerLeftClickEvent extends PlayerClickEvent {
    protected PlayerLeftClickEvent(Player player) {
        super(player);
    }
}
