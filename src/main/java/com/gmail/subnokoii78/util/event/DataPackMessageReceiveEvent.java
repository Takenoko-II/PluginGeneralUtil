package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.file.json.JSONObject;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DataPackMessageReceiveEvent implements CustomEvent {
    private final Location location;

    private final Set<Entity> targets;

    private final JSONObject message;

    protected DataPackMessageReceiveEvent(@NotNull Location location, @NotNull Set<Entity> targets, @NotNull JSONObject message) {
        this.location = location;
        this.targets = targets;
        this.message = message;
    }

    public @NotNull Location getBukkitLocation() {
        return location;
    }

    public @NotNull Set<Entity> getTargets() {
        return targets;
    }

    public @NotNull JSONObject getMessage() {
        return message;
    }

    @Override
    public @NotNull CustomEventType<? extends DataPackMessageReceiveEvent> getType() {
        return CustomEventType.DATA_PACK_MESSAGE_RECEIVE;
    }

    // summon marker ~ ~ ~ {Tags: ['plugin_api.messenger', 'plugin_api.json_message {"key":"value"}']}
    // tag ターゲット add plugin_api.target
    // tp @e[tag=plugin_api.messenger,limit=1] ~ ~ ~ ~ ~
    // kill @e[tag=plugin_api.messenger]
    // scoreboard players get # plugin_api.return
}
