package com.gmail.subnokoii78.util.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerClickEvent extends CancellableCustomEvent {
    private final Player player;

    private final boolean isLeft;

    protected PlayerClickEvent(Player player, Cancellable event, boolean isLeft) {
        super(event);
        this.player = player;
        this.isLeft = isLeft;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isLeftClick() {
        return isLeft;
    }

    public boolean isRightClick() {
        return !isLeft;
    }

    @Override
    public CustomEventType<?> getType() {
        return CustomEventType.PLAYER_CLICK;
    }
}
