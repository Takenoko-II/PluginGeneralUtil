package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.file.json.JSONObject;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DataPackMessageReceiveEvent implements CustomEvent {
    private final Entity messenger;

    private final Set<Entity> entities;

    private final JSONObject message;

    protected DataPackMessageReceiveEvent(Entity messenger, Set<Entity> entities, JSONObject message) {
        this.messenger = messenger;
        this.entities = entities;
        this.message = message;
    }

    public @NotNull Entity getMessenger() {
        return messenger;
    }

    public @NotNull Set<Entity> getTargets() {
        return entities;
    }

    public @NotNull JSONObject getMessage() {
        return message;
    }

    @Override
    public @NotNull CustomEventType<?> getType() {
        return CustomEventType.DATA_PACK_MESSAGE_RECEIVE;
    }

    // summon marker ~ ~ ~ {Tags: ['plugin_api.messenger', 'plugin_api.json_message {"key":"value"}']}
    // tag ターゲット add plugin_api.target
    // tp @e[tag=plugin_api.messenger,limit=1] ~ ~ ~
    // kill @e[tag=plugin_api.messenger]
}
