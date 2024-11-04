package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import com.gmail.subnokoii78.util.scoreboard.ScoreObjective;
import com.gmail.subnokoii78.util.scoreboard.ScoreboardUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class DataPackMessageReceiverRegistry {
    private static final DataPackMessageReceiverRegistry INSTANCE = new DataPackMessageReceiverRegistry();

    private DataPackMessageReceiverRegistry() {
        CustomEventHandlerRegistry.register(CustomEventType.DATA_PACK_MESSAGE_RECEIVE, this::onReceive);
    }

    private final Map<String, Function<DataPackMessageReceiveEvent, Integer>> receivers = new HashMap<>();

    private void onReceive(@NotNull DataPackMessageReceiveEvent event) {
        final JSONObject message = event.getMessage();

        if (!message.hasKey("id")) return;
        else if (!message.getTypeOfKey("id").equals(JSONValueType.STRING)) return;
        final String id = message.getKey("id", JSONValueType.STRING);

        for (final String receiverId : receivers.keySet()) {
            if (receiverId.equals(id)) {
                try {
                    final int value = receivers.get(id).apply(event);
                    if (!ScoreboardUtils.isRegistered("plugin_api.return")) break;
                    final ScoreObjective objective = ScoreboardUtils.getObjective("plugin_api.return");
                    objective.setScore("#", value);
                }
                catch (Throwable e) {
                    return;
                }
                break;
            }
        }
    }

    public static void register(@NotNull String id, @NotNull Function<DataPackMessageReceiveEvent, Integer> receiver) {
        INSTANCE.receivers.put(id, receiver);
    }
}
