package com.yakovliam.spaceapi.gui.components;

import org.bukkit.inventory.ItemStack;

public class Button {

    private ItemStack item;

    public ItemStack getItem() {
        return item;
    }

    public Button(ItemStack item) {
        this.item = item;
    }

}
