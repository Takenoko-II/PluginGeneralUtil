package com.gmail.subnokoii78.util.event;

import com.gmail.subnokoii78.util.execute.EntitySelector;
import com.gmail.subnokoii78.util.execute.SourceOrigin;
import com.gmail.subnokoii78.util.execute.SelectorArgument;
import com.gmail.subnokoii78.util.execute.SourceStack;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONParser;
import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import com.gmail.subnokoii78.util.schedule.RealTimeScheduler;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Consumer;

public final class CustomEventHandlerRegistry<T extends CustomEvent> {
    private static final Map<CustomEventType<?>, CustomEventHandlerRegistry<?>> registries = new HashMap<>();

    private final CustomEventType<T> type;

    private final Set<Consumer<T>> listeners = new HashSet<>();

    private CustomEventHandlerRegistry(CustomEventType<T> eventType) {
        this.type = eventType;
        registries.put(eventType, this);
    }

    public CustomEventType<T> getType() {
        return type;
    }

    public void call(T event) {
        listeners.forEach(listener -> listener.accept(event));
    }

    private static <T extends CustomEvent> CustomEventHandlerRegistry<T> getRegistry(CustomEventType<T> type) {
        if (registries.containsKey(type)) {
            return (CustomEventHandlerRegistry<T>) registries.get(type);
        }
        else {
            return new CustomEventHandlerRegistry<>(type);
        }
    }

    public static void init(Plugin plugin) {
        BukkitEventListener.register(plugin);
    }

    public static <T extends CustomEvent> void register(CustomEventType<T> type, Consumer<T> listener) {
        getRegistry(type).listeners.add(listener);
    }

    public static <T extends CustomEvent> void unregister(CustomEventType<T> type) {
        getRegistry(type).listeners.clear();
    }

    private static final class EventFiredTimeStorage {
        private static final Map<Class<? extends Event>, EventFiredTimeStorage> storages = new HashMap<>();

        private final Map<Player, Long> timeStorage = new HashMap<>();

        private EventFiredTimeStorage(Class<? extends Event> clazz) {
            if (storages.containsKey(clazz)) {
                throw new IllegalArgumentException();
            }

            storages.put(clazz, this);
        }

        private long getTime(Player player) {
            return timeStorage.getOrDefault(player, 0L);
        }

        private void setTime(Player player) {
            timeStorage.put(player, System.currentTimeMillis());
        }

        private static <T extends Event> EventFiredTimeStorage getStorage(Class<T> clazz) {
            if (storages.containsKey(clazz)) {
                return storages.get(clazz);
            }
            else {
                return new EventFiredTimeStorage(clazz);
            }
        }
    }

    private static final class BukkitEventListener implements Listener {
        private static final Set<Plugin> registeredPlugins = new HashSet<>();

        private static final BukkitEventListener listener = new BukkitEventListener();

        public static void register(Plugin plugin) {
            if (registeredPlugins.contains(plugin)) {
                throw new IllegalArgumentException();
            }

            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
            registeredPlugins.add(plugin);
        }

        private BukkitEventListener() {}

        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent event) {
            EventFiredTimeStorage.getStorage(PlayerDropItemEvent.class).setTime(event.getPlayer());
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            final Player player = event.getPlayer();

            if (event.getAction().isRightClick()) {
                EventFiredTimeStorage.getStorage(PlayerInteractEvent.class).setTime(player);
                getRegistry(CustomEventType.PLAYER_CLICK).call(new PlayerClickEvent(player, event, PlayerClickEvent.Click.RIGHT));
                getRegistry(CustomEventType.PLAYER_RIGHT_CLICK).call(new PlayerRightClickEvent(player, event));
                return;
            }

            new RealTimeScheduler(() -> {
                final long dropEventTime = EventFiredTimeStorage.getStorage(PlayerDropItemEvent.class).getTime(player);
                final long interactEventTime = EventFiredTimeStorage.getStorage(PlayerInteractEvent.class).getTime(player);

                // ドロップと同時のとき発火しない
                if (System.currentTimeMillis() - dropEventTime < 50L) return;
                // 右クリックと同時のとき発火しない
                else if (System.currentTimeMillis() - interactEventTime < 50L) return;

                new GameTickScheduler(() -> {
                    getRegistry(CustomEventType.PLAYER_CLICK).call(new PlayerClickEvent(player, event, PlayerClickEvent.Click.LEFT));
                    if (event.getClickedBlock() == null) {
                        getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(event.getPlayer(), event));
                    }
                    else {
                        getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(event.getPlayer(), event.getClickedBlock(), event));
                    }
                }).runTimeout();
            }).runTimeout(8L);
        }

        @EventHandler
        public void onPrePlayerAttack(PrePlayerAttackEntityEvent event) {
            getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(event.getPlayer(), event.getAttacked(), event));
        }

        @EventHandler
        public void onEntityTeleport(EntityTeleportEvent event) {
            final Entity entity = event.getEntity();
            final Set<String> tags = entity.getScoreboardTags();

            if (!tags.contains("plugin_api.messenger")) return;

            final EntitySelector<Entity> selector = EntitySelector.E.build().arg(SelectorArgument.TAG, "plugin_api.target");
            final Set<Entity> targets = new HashSet<>(new SourceStack(SourceOrigin.of(entity)).getEntities(selector));

            final Location location = Objects.requireNonNullElse(event.getTo(), event.getFrom());

            entity.remove();

            for (final String tag : tags) {
                if (!tag.startsWith("plugin_api.json_message")) continue;

                final String message = tag.replaceFirst("^plugin_api\\.json_message\\s+", "");

                try {
                    final JSONObject jsonObject = new JSONParser(message).parseObject();
                    getRegistry(CustomEventType.DATA_PACK_MESSAGE_RECEIVE).call(new DataPackMessageReceiveEvent(location, targets, jsonObject));
                }
                catch (RuntimeException e) {
                    return;
                }
            }
        }
    }
}
