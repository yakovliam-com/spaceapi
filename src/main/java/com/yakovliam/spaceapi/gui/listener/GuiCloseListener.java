package com.yakovliam.spaceapi.gui.listener;

import com.yakovliam.spaceapi.gui.Gui;
import com.yakovliam.spaceapi.gui.manager.GuisManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiCloseListener implements Listener {

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        // check to see if this inventory is one of the guis
        boolean hasOpen = GuisManager.hasOpen(event.getPlayer().getUniqueId());

        if (hasOpen) {
            // check similarity
            Gui openGui = GuisManager.get(event.getPlayer().getUniqueId());
            boolean same = event.getInventory().equals(openGui.getInventory());
            if (same) {
                GuisManager.close(event.getPlayer().getUniqueId());
            }
        }
    }
}
