package com.gmail.subnokoii78.util.other;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class PaperVelocityManager {
    private final Plugin plugin;

    public PaperVelocityManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    public void transfer(@NotNull Player player, @NotNull BoAServerType serverType) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(serverType.name);
        final byte[] data = output.toByteArray();
        player.sendPluginMessage(plugin, "BungeeCord", data);
    }

    public enum BoAServerType {
        GAME("game"),

        LOBBY("lobby"),

        DEVELOPMENT("develop");

        private final String name;

        BoAServerType(@NotNull String name) {
            this.name = name;
        }
    }
}
