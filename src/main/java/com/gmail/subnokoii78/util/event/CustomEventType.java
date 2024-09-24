package com.gmail.subnokoii78.util.event;

public class CustomEventType<T extends CustomEvent> {
    private final Class<T> clazz;

    private CustomEventType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public static final CustomEventType<PlayerClickEvent> PLAYER_CLICK = new CustomEventType<>(PlayerClickEvent.class);

    public static final CustomEventType<PlayerLeftClickEvent> PLAYER_LEFT_CLICK = new CustomEventType<>(PlayerLeftClickEvent.class);

    public static final CustomEventType<PlayerRightClickEvent> PLAYER_RIGHT_CLICK = new CustomEventType<>(PlayerRightClickEvent.class);
}
