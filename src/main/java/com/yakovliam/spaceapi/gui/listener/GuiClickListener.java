package com.yakovliam.spaceapi.gui.listener;

import com.yakovliam.spaceapi.gui.Gui;
import com.yakovliam.spaceapi.gui.components.ActionButton;
import com.yakovliam.spaceapi.gui.components.Button;
import com.yakovliam.spaceapi.gui.manager.GuisManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiClickListener implements Listener {

    @EventHandler
    public void onGuiInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        // check to see if this inventory is one of the guis
        boolean hasOpen = GuisManager.hasOpen(player.getUniqueId());
        if (!hasOpen) {
            return;
        }

        event.setCancelled(true); // cancel due to open gui

        // check similarity
        Gui openGui = GuisManager.get(player.getUniqueId());
        boolean same = event.getView().getTopInventory().equals(event.getClickedInventory());
        if (!same) {
            return;
        }

        // if the top inventory isn't the one clicked (double-double check)
        //if (!event.getView().getTopInventory().equals(event.getClickedInventory())) return; // this check would've been in place if not for the replacement above

        //int clickedSlot = event.getRawSlot();

        // does that item even exist?
        if (!openGui.getButtons().containsKey(event.getSlot())) {
            // nope, return
            return;
        }

        // get button at location
        Button button = openGui.getButtons().get(event.getSlot());

        // if the button is an action button
        if (button instanceof ActionButton) {
            // yes! do interaction
            ((ActionButton) button).getAction().accept(player, event);
        }  // fall through, as we don't really care what happens as long as the event is cancelled (which it is!!)
    }
}

