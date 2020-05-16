package com.yakovliam.spaceapi.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ChatListener implements Listener {

    /**
     * The queue to listen for messages in chat or commands executed by players
     */
    private static final Map<UUID, BiConsumer<Player, String>> listenQueue = new HashMap<>();

    /**
     * When a player sends a message in chat
     *
     * @param event Player's chat event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        accept(event.getPlayer(), event.getMessage(), event);
    }

    /**
     * When a player sends a command
     *
     * @param event Player's command event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        accept(event.getPlayer(), event.getMessage(), event);
    }

    /**
     * Makes sure queue is removed after the player leaves
     *
     * @param event Player's leave event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        listenQueue.entrySet().removeIf(entry -> entry.getKey().equals(event.getPlayer().getUniqueId()));
    }

    /**
     * Runs a queued piece of code when a player sends a message or command
     *
     * @param player The target player
     * @param message The message the player sent
     * @param event The target event
     */
    private void accept(Player player, String message, Cancellable event) {
        UUID uuid = player.getUniqueId();

        if (listenQueue.containsKey(uuid)) {
            // cancel message
            event.setCancelled(true);

            message = ChatColor.stripColor(message); // removes color which prevents errors while parsing

            try {
                listenQueue.get(uuid).accept(player, message);
            } catch (Exception ignored) {
            } finally {
                listenQueue.remove(uuid);
            }
        }
    }

    /**
     * Adds a new player to the queue
     *
     * @param uuid The player's uuid
     * @param consumer The action to be executed when the player is unqueued after a message or command
     */
    public static void queue(UUID uuid, BiConsumer<Player, String> consumer) {
        listenQueue.put(uuid, consumer);
    }
}
