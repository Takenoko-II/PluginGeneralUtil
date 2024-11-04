package com.gmail.subnokoii78.util.ui;

import com.gmail.subnokoii78.util.command.PluginDebugger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ContainerUI {
    private final TextComponent name;

    private final int maxColumn;

    private final Map<Integer, ItemButton> buttons = new HashMap<>();

    private final Set<Inventory> inventories = new HashSet<>();

    private final Set<Consumer<Player>> onCloseEventListenerSet = new HashSet<>();

    public ContainerUI(@NotNull TextComponent name, int maxColumn) {
        this.name = name;
        this.maxColumn = maxColumn;
        instances.add(this);
    }

    public @NotNull TextComponent getName() {
        return name;
    }

    public int getSize() {
        return maxColumn * 9;
    }

    public int getFirstEmptySlot() throws IllegalStateException {
        for (int i = 0; i < getSize(); i++) {
            if (buttons.containsKey(i)) continue;
            return i;
        }
        throw new IllegalStateException("空のスロットが存在しません");
    }

    public boolean hasEmptySlot() {
        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) return true;
        }
        return false;
    }

    public @NotNull ContainerUI set(int slot, @Nullable ItemButton button) throws IllegalArgumentException {
        if (!isValid()) {
            throw new IllegalStateException("This instance is invalid");
        }

        if (getSize() <= slot) {
            throw new IllegalArgumentException("範囲外のスロットが渡されました");
        }

        if (button == null) buttons.remove(slot);
        else buttons.put(slot, button);
        return this;
    }

    public @NotNull ContainerUI add(@NotNull ItemButton button) throws IllegalStateException {
        if (!isValid()) {
            throw new IllegalStateException("This instance is invalid");
        }

        buttons.put(getFirstEmptySlot(), button);
        return this;
    }

    public @NotNull ContainerUI fillRow(int index, @NotNull ItemButton button) {
        if (!isValid()) {
            throw new IllegalStateException("This instance is invalid");
        }

        for (int i = index * 9; i < index * 9 + 9; i++) {
            set(i, button);
        }
        return this;
    }

    public @NotNull ContainerUI fillColumn(int index, @NotNull ItemButton button) {
        if (!isValid()) {
            throw new IllegalStateException("This instance is invalid");
        }

        for (int i = 0; i < maxColumn; i++) {
            set(i * 9 + index, button);
        }
        return this;
    }

    public @NotNull ContainerUI clear() {
        if (!isValid()) {
            throw new IllegalStateException("This instance is invalid");
        }

        buttons.clear();
        return this;
    }

    public @NotNull ContainerUI onClose(@NotNull Consumer<Player> listener) {
        if (!isValid()) {
            throw new IllegalStateException("This instance is invalid");
        }

        onCloseEventListenerSet.add(listener);
        return this;
    }

    public void open(@NotNull Player player) {
        if (!isValid()) {
            throw new IllegalStateException("This instance is invalid");
        }

        final Inventory inventory = Bukkit.createInventory(null, getSize(), name);

        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) continue;

            final ItemButton button = buttons.get(i);

            if (button instanceof ItemButtonCreator creator) {
                inventory.setItem(i, creator.create(player).build());
            }
            else {
                inventory.setItem(i, button.build());
            }
        }

        inventories.add(inventory);
        player.closeInventory();
        player.openInventory(inventory);
    }

    public boolean isValid() {
        return instances.contains(this);
    }

    public void freeUpMemory() {
        buttons.clear();
        inventories.forEach(Inventory::close);
        inventories.clear();
        instances.remove(this);
    }

    private static final Set<ContainerUI> instances = new HashSet<>();

    public static final class UIEventHandler implements Listener {
        private UIEventHandler() {}

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            final ItemStack itemStack = event.getCurrentItem();

            for (final ContainerUI ui : instances) {
                if (ui.inventories.contains(event.getClickedInventory())) {
                    final ItemButton button = ui.buttons.get(event.getSlot());

                    if (itemStack == null || button == null) return;

                    button.click(new ItemButtonClickEvent(player, ui, event.getSlot(), button));
                    event.setCancelled(true);

                    break;
                }
            }
        }

        @EventHandler
        public void onMove(InventoryMoveItemEvent event) {
            for (final ContainerUI ui : instances) {
                if (ui.inventories.contains(event.getInitiator())) {
                    event.setCancelled(true);
                    break;
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (!(event.getPlayer() instanceof Player player)) return;

            for (ContainerUI ui : instances) {
                if (ui.inventories.contains(event.getInventory())) {
                    ui.inventories.remove(event.getInventory());
                    ui.onCloseEventListenerSet.forEach(listener -> listener.accept(player));
                    break;
                }
            }
        }

        private static final UIEventHandler INSTANCE = new UIEventHandler();

        private static boolean initialized = false;

        public static void init(@NotNull Plugin plugin) {
            if (initialized) return;
            initialized = true;
            Bukkit.getServer().getPluginManager().registerEvents(INSTANCE, plugin);

            PluginDebugger.DEFAULT_DEBUGGER.register("instanceCount", stack -> {
                stack.getSender().sendMessage("instanceCount: " + instances.size());
                instances.forEach(instance -> {
                    stack.getSender().sendMessage("inventoryCount: " + instance.inventories.size());
                });
                return 1;
            });

            final ContainerUI ui = new ContainerUI(Component.text("test"), 1)
                .add(new ItemButtonCreator() {
                    @NotNull
                    @Override
                    public ItemButton create(@NotNull Player player) {
                        final Material material = player.getEquipment().getItem(EquipmentSlot.HAND).getType();
                        if (material.equals(Material.AIR)) {
                            return new ItemButton(Material.APPLE);
                        }
                        return new ItemButton(material);
                    }
                })
                .add(new ItemButton(Material.PAPER));

            PluginDebugger.DEFAULT_DEBUGGER.register("openTestUI", stack -> {
                if (stack.getSender() instanceof Player player) {
                    ui.open(player);
                    return 1;
                }
                return 0;
            });
        }
    }
}
