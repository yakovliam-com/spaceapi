package dev.spaceseries.spaceapi.command;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitSpaceCommandSender extends SpaceCommandSender {

    private final CommandSender sender;

    public BukkitSpaceCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    public String getName() {
        return sender.getName();
    }

    @Override
    public UUID getUuid() {
        return isPlayer() ? ((Player) sender).getUniqueId() : null;
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        sender.spigot().sendMessage(message);
    }

    @Override
    public void sendMessage(BaseComponent message) {
        sender.spigot().sendMessage(message);
    }

    @Override
    public boolean hasPermission(String s) {
        return sender.hasPermission(s);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public CommandSender getBukkitSender() {
        return sender;
    }
}
