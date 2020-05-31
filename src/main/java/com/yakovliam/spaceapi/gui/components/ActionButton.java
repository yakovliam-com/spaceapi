package com.yakovliam.spaceapi.gui.components;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ActionButton extends Button {

    public BiConsumer<Player, InventoryClickEvent> getAction() {
        return action;
    }

    private BiConsumer<Player, InventoryClickEvent> action;

    public ActionButton(ItemStack item, BiConsumer<Player, InventoryClickEvent> action) {
        super(item);
        this.action = action;
    }

    public ActionButton(Function<Player, ItemStack> item, BiConsumer<Player, InventoryClickEvent> action) {
        super(item);
        this.action = action;
    }
}
