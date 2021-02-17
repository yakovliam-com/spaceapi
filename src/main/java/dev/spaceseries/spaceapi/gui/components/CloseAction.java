package dev.spaceseries.spaceapi.gui.components;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.BiConsumer;

public class CloseAction {

    /**
     * The action executed when the gui is closed
     */
    private final BiConsumer<Player, InventoryCloseEvent> action;

    /**
     * Initializes a new close action
     *
     * @param action The action itself
     */
    public CloseAction(BiConsumer<Player, InventoryCloseEvent> action) {
        this.action = action;
    }

    public BiConsumer<Player, InventoryCloseEvent> getAction() {
        return action;
    }
}
