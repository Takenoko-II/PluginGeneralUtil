package com.gmail.subnokoii78.util.other;

import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONParser;
import com.gmail.subnokoii78.util.file.json.JSONSerializer;
import com.gmail.subnokoii78.util.ui.ContainerUI;
import com.gmail.subnokoii78.util.ui.ItemButton;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class PaperVelocityManager implements PluginMessageListener {
    private static final Map<Plugin, PaperVelocityManager> managerMap = new HashMap<>();

    private final Plugin plugin;

    private final Set<Consumer<JSONObject>> customPluginMessageReceivers = new HashSet<>();

    private PaperVelocityManager(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin);

        if (managerMap.containsKey(plugin)) {
            throw new IllegalArgumentException("既にインスタンスが作成されています");
        }

        this.plugin = plugin;
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
        managerMap.put(plugin, this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String subChannel, @NotNull Player player, @NotNull byte[] message) {
        if (subChannel.equals("Forward")) {
            final ByteArrayDataInput input = ByteStreams.newDataInput(message);
            final String channel = input.readUTF();
            if (channel.equals("CustomPluginMessageByPaperVelocityManager")) {
                final byte[] data = new byte[input.readShort()];
                input.readFully(data);
                final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));

                final String jsonString;
                try {
                    jsonString = dataInputStream.readUTF();
                }
                catch (IOException e) {
                    throw new RuntimeException("データの読み取りに失敗しました");
                }

                final JSONObject jsonObject = new JSONParser(jsonString).parseObject();

                customPluginMessageReceivers.forEach(receiver -> {
                    receiver.accept(jsonObject);
                });
                return;
            }
        }

        InteractivePluginMessageBuilder.receiveMessage(subChannel, player, message);
    }

    public void getServerOf(@NotNull String player, @NotNull Consumer<BoAServerType> callback) {
        newInteractiveMessage("GetPlayerServer", (input, time) -> {
            input.readUTF();
            final String id = input.readUTF();
            callback.accept(BoAServerType.getById(id));
        })
            .argument(player)
            .sendMessage();
    }

    public void getIPAndPort(@NotNull Player player, @NotNull BiConsumer<String, Integer> callback) {
        newInteractiveMessage("IP", (input, time) -> {
            final String ip = input.readUTF();
            final int port = input.readInt();
            callback.accept(ip, port);
        })
            .sendMessage(player);
    }

    public void getServer(@NotNull Consumer<BoAServerType> callback) {
        newInteractiveMessage("GetServer", (input, time) -> {
            input.readUTF();
            final String id = input.readUTF();
            callback.accept(BoAServerType.getById(id));
        })
            .sendMessage();
    }

    public void transfer(@NotNull Player player, @NotNull BoAServerType serverType) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(serverType.id);
        player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
        player.sendMessage(Component.text(String.format("%s サーバーへの接続を試行中...", serverType.id)));
    }

    public void kick(@NotNull Player player, @NotNull TextComponent reason) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("KickPlayerRaw");
        output.writeUTF(player.getName());
        output.writeUTF(JSONComponentSerializer.json().serialize(reason));
        player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }

    public void sendCustomPluginMessage(@NotNull BoAServerType target, @NotNull JSONObject data) {
        final JSONObject message = new JSONObject();

        getServer(server -> {
            message.set("id", UUID.randomUUID().toString());
            message.set("server", server);
            message.set("plugin", plugin.getName());
            message.set("timestamp", System.currentTimeMillis());
            message.set("data", data);

            newInteractiveMessage("Forward", (input, time) -> {})
                .argument(target.id)
                .argument("CustomPluginMessageByPaperVelocityManager")
                .argument(out -> {
                    try {
                        out.writeUTF(new JSONSerializer(message).serialize());
                    }
                    catch (IOException e) {
                        throw new RuntimeException("データの書き込みに失敗しました");
                    }
                })
                .sendMessageOneWay();
        });
    }

    public void onCustomPluginMessageReceive(@NotNull Consumer<JSONObject> callback) {
        customPluginMessageReceivers.add(callback);
    }

    public void openServerSelector(@NotNull Player player) {
        new ContainerUI(Component.text("Battle of Apostolos"), 1)
            .set(1, new ItemButton(Material.NETHER_STAR)
                .name(Component.text("Game").color(NamedTextColor.AQUA))
                .addLore(Component.text("ゲームサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .onClick(event -> {
                    event.playClickingSound();
                    event.closeUI();
                    transfer(event.getPlayer(), BoAServerType.GAME);
                })
            )
            .set(3, new ItemButton(Material.PAPER)
                .name(Component.text("Lobby").color(NamedTextColor.GOLD))
                .addLore(Component.text("ロビーサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .glint(true)
                .onClick(event -> {
                    event.playClickingSound();
                    event.closeUI();
                    transfer(event.getPlayer(), BoAServerType.LOBBY);
                })
            )
            .set(5, new ItemButton(Material.COMMAND_BLOCK)
                .name(Component.text("Development").color(NamedTextColor.GOLD))
                .addLore(Component.text("開発サーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .glint(true)
                .onClick(event -> {
                    event.playClickingSound();
                    event.closeUI();
                    if (event.getPlayer().isOp()) {
                        transfer(event.getPlayer(), BoAServerType.DEVELOPMENT);
                    }
                    else {
                        event.getPlayer().sendMessage(Component.text("このサーバーへの接続はオペレーター権限が必要です").color(NamedTextColor.RED));
                    }
                })
            )
            .set(7, new ItemButton(Material.RED_BED)
                .name(Component.text("Spawn").color(NamedTextColor.RED))
                .addLore(Component.text("スポーン地点に戻る").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .onClick(event -> {
                    final Location spawnPoint = event.getPlayer().getRespawnLocation();
                    event.closeUI();
                    event.getPlayer().teleport(spawnPoint == null ? event.getPlayer().getWorld().getSpawnLocation() : spawnPoint);
                    event.playClickingSound();
                })
            )
            .open(player);
    }

    public static PaperVelocityManager register(@NotNull Plugin plugin) {
        return new PaperVelocityManager(plugin);
    }

    public static void unregister(@NotNull Plugin plugin) {
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, "BungeeCord");
        managerMap.remove(plugin);
    }

    public static final class InteractivePluginMessageBuilder {
        private static final List<UnreceivedPluginMessage> messageQueue = new ArrayList<>();

        private final Plugin plugin;

        private final ByteArrayDataOutput output = ByteStreams.newDataOutput();

        private final String subChannel;

        private final BiConsumer<ByteArrayDataInput, Long> callback;

        private InteractivePluginMessageBuilder(@NotNull Plugin plugin, @NotNull String subChannel, @NotNull BiConsumer<ByteArrayDataInput, Long> callback) {
            this.plugin = plugin;
            this.output.writeUTF(subChannel);
            this.subChannel = subChannel;
            this.callback = callback;
        }

        public InteractivePluginMessageBuilder argument(@NotNull String value) {
            output.writeUTF(value);
            return this;
        }

        public InteractivePluginMessageBuilder argument(short value) {
            output.writeInt(value);
            return this;
        }

        public InteractivePluginMessageBuilder argument(@NotNull TextComponent textComponent) {
            output.writeUTF(JSONComponentSerializer.json().serialize(textComponent));
            return this;
        }

        public InteractivePluginMessageBuilder argument(@NotNull Consumer<DataOutputStream> writer) {
            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(data);
            writer.accept(out);
            final byte[] bytes = data.toByteArray();
            output.writeShort(bytes.length);
            output.write(bytes);
            return this;
        }

        public void sendMessage(@NotNull Player player) {
            player.sendPluginMessage(plugin, subChannel, output.toByteArray());
            messageQueue.add(new UnreceivedPluginMessage(subChannel, System.currentTimeMillis(), callback));
        }

        public void sendMessage() {
            Bukkit.getServer().sendPluginMessage(plugin, subChannel, output.toByteArray());
            messageQueue.add(new UnreceivedPluginMessage(subChannel, System.currentTimeMillis(), callback));
        }

        public void sendMessageOneWay() {
            Bukkit.getServer().sendPluginMessage(plugin, subChannel, output.toByteArray());
        }

        private static void receiveMessage(@NotNull String subChannel, @NotNull Player player, byte[] message) {
            for (final UnreceivedPluginMessage pluginMessage : messageQueue) {
                if (pluginMessage.subChannel.equals(subChannel)) {
                    pluginMessage.callback.accept(ByteStreams.newDataInput(message), System.currentTimeMillis() - pluginMessage.time);
                    messageQueue.remove(pluginMessage);
                    return;
                }
            }

            throw new IllegalArgumentException("送信したプラグインメッセージに対応しないレスポンスです");
        }

        public record UnreceivedPluginMessage(@NotNull String subChannel, long time, @NotNull BiConsumer<ByteArrayDataInput, Long> callback) {}
    }

    private InteractivePluginMessageBuilder newInteractiveMessage(@NotNull String subChannel, @NotNull BiConsumer<ByteArrayDataInput, Long> callback) {
        return new InteractivePluginMessageBuilder(plugin, subChannel, callback);
    }

    public enum BoAServerType {
        GAME("game"),

        LOBBY("lobby"),

        DEVELOPMENT("develop");

        private final String id;

        BoAServerType(@NotNull String id) {
            this.id = id;
        }

        static BoAServerType getById(@NotNull String id) {
            for (BoAServerType value : values()) {
                if (value.id.equals(id)) return value;
            }

            throw new IllegalArgumentException("無効なIDです");
        }
    }
}
