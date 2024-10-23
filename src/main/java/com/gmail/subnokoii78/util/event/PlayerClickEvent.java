package com.gmail.subnokoii78.util.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class PlayerClickEvent extends CancellableCustomEvent {
    private final Player player;

    private final Click click;

    protected PlayerClickEvent(@NotNull Player player, @NotNull Cancellable event, @NotNull Click click) {
        super(event);
        this.player = player;
        this.click = click;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Click getClick() {
        return click;
    }

    @Override
    public @NotNull CustomEventType<? extends PlayerClickEvent> getType() {
        return CustomEventType.PLAYER_CLICK;
    }

    public enum Click {
        RIGHT,
        LEFT
    }
}
