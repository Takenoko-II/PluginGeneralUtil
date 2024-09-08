package com.gmail.subnokoii78.util.other;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class ServerMessageBuilder {
    private final TextComponent.Builder message = Component.text();

    private final ServerMessageType messageType;

    public ServerMessageBuilder(@NotNull ServerMessageType messageType) {
        message
            .append(Component.text("[").color(NamedTextColor.WHITE))
            .append(Component.text(messageType.prefix).color(messageType.textColor))
            .append(Component.text("]").color(NamedTextColor.WHITE))
            .appendSpace();

        this.messageType = messageType;
    }

    public @NotNull ServerMessageBuilder append(@NotNull String text) {
        message.append(Component.text(text.replaceAll("\\n", "")).color(messageType.textColor));
        return this;
    }

    public @NotNull ServerMessageBuilder append(@NotNull String template, Object... values) {
        String t = template;

        for (final Object value : values) {
            t = t.replaceFirst("\\$\\{[^{}\\[\\]()\"'`^~;:<>?!$%&|\\\\/,]+}", value.toString());
        }

        message.append(Component.text(t).color(messageType.textColor));
        return this;
    }

    public void sendMessage() {
        Bukkit.getServer().sendMessage(message.build());
    }

    public enum ServerMessageType {
        INFO("Info", NamedTextColor.AQUA),

        TIP("Tip", NamedTextColor.GREEN),

        ANNOUNCEMENT("Announcement", NamedTextColor.LIGHT_PURPLE),

        WARNING("Warning", NamedTextColor.GOLD),

        CAUTION("Caution", NamedTextColor.RED);

        private final String prefix;

        private final TextColor textColor;

        ServerMessageType(@NotNull String prefix, @NotNull TextColor textColor) {
            this.prefix = prefix;
            this.textColor = textColor;
        }
    }
}
