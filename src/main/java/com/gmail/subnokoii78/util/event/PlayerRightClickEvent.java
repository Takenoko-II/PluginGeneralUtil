package com.gmail.subnokoii78.util.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class PlayerRightClickEvent extends PlayerClickEvent {
    private final Action action;

    private final Block block;

    private final Entity entity;

    protected PlayerRightClickEvent(@NotNull Player player, @NotNull Cancellable event) {
        super(player, event, Click.RIGHT);
        action = Action.INTERACT_AIR;
        block = null;
        entity = null;
    }

    protected PlayerRightClickEvent(@NotNull Player player, @NotNull Block block, @NotNull Cancellable event) {
        super(player, event, Click.RIGHT);
        action = Action.INTERACT_BLOCK;
        this.block = block;
        entity = null;
    }

    protected PlayerRightClickEvent(@NotNull Player player, @NotNull Entity entity, @NotNull Cancellable event) {
        super(player, event, Click.RIGHT);
        action = Action.INTERACT_ENTITY;
        block = null;
        this.entity = entity;
    }

    public @NotNull Action getAction() {
        return action;
    }

    public Block getClickedBlock() throws IllegalStateException {
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
    public @NotNull CustomEventType<? extends PlayerRightClickEvent> getType() {
        return CustomEventType.PLAYER_RIGHT_CLICK;
    }

    public enum Action {
        INTERACT_BLOCK,
        INTERACT_ENTITY,
        INTERACT_AIR
    }
}
