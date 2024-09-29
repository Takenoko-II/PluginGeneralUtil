package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.file.json.JSONObject;
import org.bukkit.entity.Entity;

import java.util.Set;

public class DataPackMessageReceiveEvent implements CustomEvent {
    private final Entity sender;

    private final Set<Entity> entities;

    private final JSONObject message;

    protected DataPackMessageReceiveEvent(Entity sender, Set<Entity> entities, JSONObject message) {
        this.sender = sender;
        this.entities = entities;
        this.message = message;
    }

    public Entity getSender() {
        return sender;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public JSONObject getMessage() {
        return message;
    }

    @Override
    public CustomEventType<?> getType() {
        return CustomEventType.DATA_PACK_MESSAGE_RECEIVE;
    }

    // summon marker ~ ~ ~ {Tags: ['plugin_api.messenger', 'plugin_api.json_message {"key":"value"}']}
    // tag ターゲット add plugin_api.target
    // tp @e[tag=plugin_api.messenger,limit=1] ~ ~ ~
    // kill @e[tag=plugin_api.messenger]
}
