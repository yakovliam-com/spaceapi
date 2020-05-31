package com.yakovliam.spaceapi.gui.components;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class Button {

    private ItemStack item;
    private Function<Player, ItemStack> itemFunction;

    public Button(Function<Player, ItemStack> item) {
        itemFunction = item;
    }

    public ItemStack getItem(Player player) {
        return itemFunction == null ? item : itemFunction.apply(player);
    }

    public Button(ItemStack item) {
        this.item = item;
    }

}
