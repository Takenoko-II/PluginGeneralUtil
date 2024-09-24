package com.gmail.subnokoii78.util.event;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

    private static final class EventFiredTimeStorage<T extends Event> {
        private static final Map<Class<? extends Event>, EventFiredTimeStorage<? extends Event>> storages = new HashMap<>();

        private final Map<Player, Long> timeStorage = new HashMap<>();

        private EventFiredTimeStorage(Class<T> clazz) {
            if (storages.containsKey(clazz)) {
                throw new IllegalArgumentException();
            }

            storages.put(clazz, this);
        }

        public long getTime(Player player) {
            return timeStorage.getOrDefault(player, 0L);
        }

        public void setTime(Player player) {
            timeStorage.put(player, System.currentTimeMillis());
        }

        public static <T extends Event> EventFiredTimeStorage<T> getStorage(Class<T> clazz) {
            if (storages.containsKey(clazz)) {
                return (EventFiredTimeStorage<T>) storages.get(clazz);
            }
            else {
                return new EventFiredTimeStorage<>(clazz);
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
            // デフォルトでPlayerInteractEventよりも早く呼び出される
            EventFiredTimeStorage.getStorage(PlayerDropItemEvent.class).setTime(event.getPlayer());
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            final Player player = event.getPlayer();

            if (event.getAction().isRightClick()) {
                EventFiredTimeStorage.getStorage(PlayerInteractEvent.class).setTime(player);
                getRegistry(CustomEventType.PLAYER_CLICK).call(new PlayerClickEvent(player));
                getRegistry(CustomEventType.PLAYER_RIGHT_CLICK).call(new PlayerRightClickEvent(player));
                return;
            }

            final long dropEventTime = EventFiredTimeStorage.getStorage(PlayerDropItemEvent.class).getTime(player);
            final long interactEventTime = EventFiredTimeStorage.getStorage(PlayerInteractEvent.class).getTime(player);

            // ドロップと同時のとき発火しない
            if (System.currentTimeMillis() - dropEventTime < 50L) return;
            // 右クリックと同時のとき発火しない
            else if (System.currentTimeMillis() - interactEventTime < 50L) return;

            getRegistry(CustomEventType.PLAYER_CLICK).call(new PlayerClickEvent(player));
            getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(player));
        }

        @EventHandler
        public void onPrePlayerAttack(PrePlayerAttackEntityEvent event) {
            getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(event.getPlayer()));
        }
    }
}
