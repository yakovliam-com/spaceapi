package dev.spaceseries.spaceapi.gui;

import dev.spaceseries.spaceapi.gui.components.ActionButton;
import dev.spaceseries.spaceapi.gui.components.Button;
import dev.spaceseries.spaceapi.gui.components.CloseAction;
import dev.spaceseries.spaceapi.gui.components.PlainButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Gui {

    private final JavaPlugin plugin;

    private final int rows;
    private final String displayName;
    private final Map<Integer, Button> buttons;
    private final int slots;
    private CloseAction closeAction;

    public Gui(JavaPlugin plugin, int rows, String displayName, Map<Integer, Button> buttons) {
        this.plugin = plugin;
        this.rows = rows;
        this.displayName = displayName;
        this.buttons = buttons;
        this.slots = rows * 9;
        this.closeAction = null;
    }

    public Gui(JavaPlugin plugin, String displayName, int rows) {
        this.plugin = plugin;
        this.rows = rows;
        this.displayName = displayName;
        this.slots = rows * 9;
        this.buttons = new HashMap<>();
        this.closeAction = null;
    }

    public void setItemInteraction(int slot, ItemStack item, BiConsumer<Player, InventoryClickEvent> action) {
        this.buttons.put(slot, new ActionButton(item, action));
    }

    public void setItemInteraction(int slot, Function<Player, ItemStack> item, BiConsumer<Player, InventoryClickEvent> action) {
        this.buttons.put(slot, new ActionButton(item, action));
    }

    public void addItemInteraction(ItemStack item, BiConsumer<Player, InventoryClickEvent> action) {
        putNextAvailableButton(new ActionButton(item, action));
    }

    public void addItemInteraction(Function<Player, ItemStack> item, BiConsumer<Player, InventoryClickEvent> action) {
        putNextAvailableButton(new ActionButton(item, action));
    }

    private void putNextAvailableButton(ActionButton actionButton) {
        int nextAvailable = 0;

        for (int i = 0; i < this.slots; ++i) {
            if (this.buttons.get(i) == null) {
                nextAvailable = i;
                break;
            }
        }

        this.buttons.put(nextAvailable, actionButton);
    }


    public void addItem(ItemStack item) {
        int nextAvailable = 0;

        for (int i = 0; i < this.slots; ++i) {
            if (this.buttons.get(i) == null) {
                nextAvailable = i;
                break;
            }
        }

        this.buttons.put(nextAvailable, new PlainButton(item));
    }

    public void setItem(int slot, ItemStack item) {
        this.buttons.put(slot, new PlainButton(item));
    }

    public void setCloseAction(BiConsumer<Player, InventoryCloseEvent> action) {
        this.closeAction = new CloseAction(action);
    }

    public void open(Player player) {
        // create holder
        GuiInventoryHolder inventoryHolder = new GuiInventoryHolder(plugin, this);

        // create inventory
        Inventory inventory = Bukkit.createInventory(inventoryHolder, this.slots, this.displayName);

        // set inventory of holder
        inventoryHolder.setInventory(inventory);

        // set buttons
        for (int i = 0; i < this.slots; ++i) {
            if (this.buttons.get(i) != null) {
                inventory.setItem(i, this.buttons.get(i).getItem(player));
            }
        }

        // close inventory (current one open, whatever it is) first
        player.closeInventory();

        // re-open, but this time it's the NEW one...this prevents interaction issues
        player.openInventory(inventory);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public int getRows() {
        return rows;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Map<Integer, Button> getButtons() {
        return buttons;
    }

    public int getSlots() {
        return slots;
    }

    public CloseAction getCloseAction() {
        return closeAction;
    }
}
