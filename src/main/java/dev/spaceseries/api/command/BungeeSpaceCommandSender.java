package dev.spaceseries.api.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeeSpaceCommandSender extends SpaceCommandSender {

    private final CommandSender sender;

    public BungeeSpaceCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    public String getName() {
        return sender.getName();
    }

    @Override
    public UUID getUuid() {
        return isPlayer() ? ((ProxiedPlayer) sender).getUniqueId() : null;
    }

    public void sendMessage(BaseComponent... message) {
        sender.sendMessage(message);
    }

    public void sendMessage(BaseComponent message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String s) {
        return sender.hasPermission(s);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof ProxiedPlayer;
    }

    public CommandSender getBungeeSender() {
        return sender;
    }
}
