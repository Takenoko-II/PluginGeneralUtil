package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

    public static final class DataPackMessageReceiverRegistry {
        public static final DataPackMessageReceiverRegistry INSTANCE = new DataPackMessageReceiverRegistry();

        private DataPackMessageReceiverRegistry() {
            CustomEventHandlerRegistry.register(CustomEventType.DATA_PACK_MESSAGE_RECEIVE, this::onReceive);
        }

        private final Map<String, Consumer<DataPackMessageReceiveEvent>> receiverMap = new HashMap<>();

        private void onReceive(@NotNull DataPackMessageReceiveEvent event) {
            if (!event.message.has("id")) return;
            else if (!event.message.getTypeOf("id").equals(JSONValueType.STRING)) return;
            final String id = event.message.get("id", JSONValueType.STRING);

            for (final String receiverId : receiverMap.keySet()) {
                if (receiverId.equals(id)) {
                    receiverMap.get(id).accept(event);
                    break;
                }
            }
        }

        public void register(@NotNull String id, @NotNull Consumer<DataPackMessageReceiveEvent> receiver) {
            receiverMap.put(id, receiver);
        }
    }
}
