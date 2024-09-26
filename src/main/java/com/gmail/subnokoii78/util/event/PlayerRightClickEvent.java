package com.gmail.subnokoii78.util.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerRightClickEvent extends PlayerClickEvent {
    private final Action action;

    private final Block block;

    private final Entity entity;

    protected PlayerRightClickEvent(Player player, Cancellable event) {
        super(player, event, false);
        action = Action.INTERACT_AIR;
        block = null;
        entity = null;
    }

    protected PlayerRightClickEvent(Player player, Block block, Cancellable event) {
        super(player, event, false);
        action = Action.INTERACT_BLOCK;
        this.block = block;
        entity = null;
    }

    protected PlayerRightClickEvent(Player player, Entity entity, Cancellable event) {
        super(player, event, false);
        action = Action.INTERACT_ENTITY;
        block = null;
        this.entity = entity;
    }

    public Action getAction() {
        return action;
    }

    public Entity getClickedEntity() throws IllegalStateException {
        if (action.equals(Action.INTERACT_ENTITY)) {
            return entity;
        }

        throw new IllegalStateException();
    }

    public Block getClickedBlock() throws IllegalStateException {
        if (action.equals(Action.INTERACT_BLOCK)) {
            return block;
        }

        throw new IllegalStateException();
    }

    @Override
    public CustomEventType<?> getType() {
        return CustomEventType.PLAYER_RIGHT_CLICK;
    }

    public enum Action {
        INTERACT_BLOCK,
        INTERACT_ENTITY,
        INTERACT_AIR
    }
}
