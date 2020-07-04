package dev.spaceseries.api.gui.components;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.BiConsumer;

public class CloseAction {

    /**
     * The action executed when the gui is closed
     */
    @Getter
    private BiConsumer<Player, InventoryCloseEvent> action;

    /**
     * Initializes a new close action
     *
     * @param action The action itself
     */
    public CloseAction(BiConsumer<Player, InventoryCloseEvent> action) {
        this.action = action;
    }
}
