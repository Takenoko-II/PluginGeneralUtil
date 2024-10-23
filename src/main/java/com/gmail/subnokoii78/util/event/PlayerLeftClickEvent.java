package com.gmail.subnokoii78.util.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class PlayerLeftClickEvent extends PlayerClickEvent {
    private final Action action;

    private final Block block;

    private final Entity entity;

    protected PlayerLeftClickEvent(@NotNull Player player, @NotNull Cancellable event) {
        super(player, event, Click.LEFT);
        this.action = Action.SWING_AIR;
        block = null;
        entity = null;
    }

    protected PlayerLeftClickEvent(@NotNull Player player, @NotNull Block block, @NotNull Cancellable event) {
        super(player, event, Click.LEFT);
        this.action = Action.BLOCK_HIT;
        this.block = block;
        entity = null;
    }

    protected PlayerLeftClickEvent(@NotNull Player player, @NotNull Entity entity, @NotNull Cancellable event) {
        super(player, event, Click.LEFT);
        this.action = Action.ENTITY_HIT;
        block = null;
        this.entity = entity;
    }

    public @NotNull Action getAction() {
        return action;
    }

    public @NotNull Block getClickedBlock() throws IllegalStateException {
        if (block == null) {
            throw new IllegalStateException("ブロックをクリックしていません");
        }

        return block;
    }

    public @NotNull Entity getClickedEntity() throws IllegalStateException {
        if (entity == null) {
            throw new IllegalStateException("エンティティをクリックしていません");
        }

        return entity;
    }

    @Override
    public @NotNull CustomEventType<? extends PlayerLeftClickEvent> getType() {
        return CustomEventType.PLAYER_LEFT_CLICK;
    }

    public enum Action {
        ENTITY_HIT,
        BLOCK_HIT,
        SWING_AIR
    }
}
