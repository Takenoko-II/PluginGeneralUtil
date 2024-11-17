package com.gmail.subnokoii78.util.event;

import org.jetbrains.annotations.NotNull;

public final class CustomEventType<T extends CustomEvent> {
    private final Class<T> clazz;

    private CustomEventType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<T> getEventClass() {
        return clazz;
    }

    public static final CustomEventType<PlayerClickEvent> PLAYER_CLICK = new CustomEventType<>(PlayerClickEvent.class);

    public static final CustomEventType<PlayerLeftClickEvent> PLAYER_LEFT_CLICK = new CustomEventType<>(PlayerLeftClickEvent.class);

    public static final CustomEventType<PlayerRightClickEvent> PLAYER_RIGHT_CLICK = new CustomEventType<>(PlayerRightClickEvent.class);

    public static final CustomEventType<DataPackMessageReceiveEvent> DATA_PACK_MESSAGE_RECEIVE = new CustomEventType<>(DataPackMessageReceiveEvent.class);
}
