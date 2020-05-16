package com.yakovliam.spaceapi.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiInventoryHolder implements InventoryHolder {

    private final Gui gui;
    private Inventory inventory;

    public GuiInventoryHolder(Gui gui) {
        this.gui = gui;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Gui getGui() {
        return gui;
    }
}
