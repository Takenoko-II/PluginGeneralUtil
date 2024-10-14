package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class DataPackMessageReceiverRegistry {
    private static final DataPackMessageReceiverRegistry INSTANCE = new DataPackMessageReceiverRegistry();

    private DataPackMessageReceiverRegistry() {
        CustomEventHandlerRegistry.register(CustomEventType.DATA_PACK_MESSAGE_RECEIVE, this::onReceive);
    }

    private final Map<String, Consumer<DataPackMessageReceiveEvent>> receivers = new HashMap<>();

    private void onReceive(@NotNull DataPackMessageReceiveEvent event) {
        final JSONObject message = event.getMessage();

        if (!message.has("id")) return;
        else if (!message.getTypeOf("id").equals(JSONValueType.STRING)) return;
        final String id = message.get("id", JSONValueType.STRING);

        for (final String receiverId : receivers.keySet()) {
            if (receiverId.equals(id)) {
                try {
                    receivers.get(id).accept(event);
                }
                catch (Throwable e) {
                    return;
                }
                break;
            }
        }
    }

    public static void register(@NotNull String id, @NotNull Consumer<DataPackMessageReceiveEvent> receiver) {
        INSTANCE.receivers.put(id, receiver);
    }
}
