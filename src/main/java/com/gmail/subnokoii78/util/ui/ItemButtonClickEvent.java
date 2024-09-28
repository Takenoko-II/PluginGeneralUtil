package com.gmail.subnokoii78.util.ui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemButtonClickEvent {
    private final Player player;

    private final ItemButton button;

    public ItemButtonClickEvent(@NotNull Player player, @NotNull ItemButton button) {
        this.player = player;
        this.button = button;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull ItemButton getClickedButton() {
        return button;
    }

    public void playClickingSound() {
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10.0f, 2.0f);
    }

    public void closeUI() {
        player.closeInventory();
    }
}
