package com.yakovliam.spaceapi.gui.listener;

import com.yakovliam.spaceapi.gui.Gui;
import com.yakovliam.spaceapi.gui.GuiInventoryHolder;
import com.yakovliam.spaceapi.gui.components.ActionButton;
import com.yakovliam.spaceapi.gui.components.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiListener implements Listener {

    private final JavaPlugin plugin;

    public GuiListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGuiInteract(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof GuiInventoryHolder)) return;
        if (!((GuiInventoryHolder) holder).getPlugin().equals(plugin)) return;

        if (!(event.getWhoClicked() instanceof Player)) return; // this would be very suspicious

        event.setCancelled(true);
        Gui gui = ((GuiInventoryHolder) holder).getGui();

        Button button = gui.getButtons().get(event.getSlot());
        if (button == null) return;

        Player player = (Player) event.getWhoClicked();

        //int clickedSlot = event.getRawSlot(); // maybe needed?

        // if the button is an action button, treat as an action and execute click event
        if (button instanceof ActionButton) {
            // yes! do interaction
            ((ActionButton) button).getAction().accept(player, event);
        }  // fall through, as we don't really care what happens as long as the event is cancelled (which it is!!)
    }
}

