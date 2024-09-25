package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.execute.SourceStack;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class DataPackMessageReceiveEvent implements CustomEvent {
    private final SourceStack stack;

    private final JSONObject message;

    protected DataPackMessageReceiveEvent(SourceStack stack, JSONObject message) {
        this.stack = stack;
        this.message = message;
    }

    public SourceStack getSource() {
        return stack;
    }

    public JSONObject getMessage() {
        return message;
    }

    public static void sendDataPackMessage(SourceStack source, JSONObject message) {
        final Entity sender = source.getDimension().spawnEntity(source.getAsBukkitLocation(), EntityType.MARKER);
        sender.addScoreboardTag("plugin_api:messenger");
        sender.addScoreboardTag("plugin_api:json_message " + new JSONSerializer(message).serialize());
        sender.teleport(source.getAsBukkitLocation());
        sender.remove();
    }
}
