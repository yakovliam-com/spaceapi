package com.yakovliam.spaceapi.gui;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiInventoryHolder implements InventoryHolder {

    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final Gui gui;

    private Inventory inventory;

    public GuiInventoryHolder(JavaPlugin plugin, Gui gui) {
        this.plugin = plugin;
        this.gui = gui;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
