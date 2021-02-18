package dev.spaceseries.spaceapi.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiInventoryHolder implements InventoryHolder {

    private final JavaPlugin plugin;
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

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Gui getGui() {
        return gui;
    }
}
