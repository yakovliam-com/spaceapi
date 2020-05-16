//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yakovliam.spaceapi.gui;
import com.yakovliam.spaceapi.gui.components.ActionButton;
import com.yakovliam.spaceapi.gui.components.Button;
import com.yakovliam.spaceapi.gui.components.NoActionButton;
import com.yakovliam.spaceapi.gui.manager.GuisManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Gui {
    private int rows;
    private String displayName;
    private Map<Integer, Button> buttons;
    private int slots;
    private Inventory inventory;

    public Gui(int rows, String displayName, Map<Integer, Button> buttons) {
        this.rows = rows;
        this.displayName = displayName;
        this.buttons = buttons;
        this.slots = rows * 9;
    }

    public Gui(String displayName, int rows) {
        this.rows = rows;
        this.displayName = displayName;
        this.slots = rows * 9;
        this.buttons = new HashMap();
    }

    public void setItemInteraction(int slot, ItemStack item, BiConsumer<Player, InventoryClickEvent> action) {
        this.buttons.put(slot, new ActionButton(item, action));
    }

    public void addItemInteraction(ItemStack item, BiConsumer<Player, InventoryClickEvent> action) {
        int nextAvailable = 0;

        for (int i = 0; i < this.slots; ++i) {
            if (this.buttons.get(i) == null) {
                nextAvailable = i;
                break;
            }
        }

        this.buttons.put(nextAvailable, new ActionButton(item, action));
    }

    public void addItem(ItemStack item) {
        int nextAvailable = 0;

        for (int i = 0; i < this.slots; ++i) {
            if (this.buttons.get(i) == null) {
                nextAvailable = i;
                break;
            }
        }

        this.buttons.put(nextAvailable, new NoActionButton(item));
    }

    public void setItem(int slot, ItemStack item) {
        this.buttons.put(slot, new NoActionButton(item));
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, this.slots, this.displayName);

        for (int i = 0; i < this.slots; ++i) {
            if (this.buttons.get(i) != null) {
                inventory.setItem(i, this.buttons.get(i).getItem());
            }
        }

        this.inventory = inventory;

        GuisManager.add(player.getUniqueId(), this);

        // close inventory (current one open, whatever it is) first
        player.closeInventory();

        // re-open, but this time it's the NEW one...this prevents interaction issues
        player.openInventory(inventory);
    }

    public int getRows() {
        return this.rows;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Map<Integer, Button> getButtons() {
        return this.buttons;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
