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

    protected PlayerLeftClickEvent(Player player, Cancellable event) {
        super(player, event, true);
        this.action = Action.SWING_AIR;
        block = null;
        entity = null;
    }

    protected PlayerLeftClickEvent(Player player, Block block, Cancellable event) {
        super(player, event, true);
        this.action = Action.BLOCK_HIT;
        this.block = block;
        entity = null;
    }

    protected PlayerLeftClickEvent(Player player, Entity entity, Cancellable event) {
        super(player, event, true);
        this.action = Action.ENTITY_HIT;
        block = null;
        this.entity = entity;
    }

    public Action getAction() {
        return action;
    }

    public Block getClickedBlock() throws IllegalStateException {
        if (action.equals(Action.BLOCK_HIT)) {
            return block;
        }

        throw new IllegalStateException();
    }

    public Entity getClickedEntity() throws IllegalStateException {
        if (action.equals(Action.ENTITY_HIT)) {
            return entity;
        }

        throw new IllegalStateException();
    }

    @Override
    public @NotNull CustomEventType<?> getType() {
        return CustomEventType.PLAYER_LEFT_CLICK;
    }

    public enum Action {
        ENTITY_HIT,
        BLOCK_HIT,
        SWING_AIR
    }
}
