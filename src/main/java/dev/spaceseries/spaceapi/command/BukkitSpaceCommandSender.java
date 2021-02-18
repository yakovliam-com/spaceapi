package dev.spaceseries.spaceapi.command;

import dev.spaceseries.spaceapi.text.Message;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
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
        if (sender instanceof Player) {
            Message.getAudienceProvider().player(((Player) sender).getUniqueId()).sendMessage(BungeeComponentSerializer.get().deserialize(message));
        } else {
            Message.getAudienceProvider().player(((Player) sender).getUniqueId()).sendMessage(BungeeComponentSerializer.get().deserialize(message));
        }
    }

    @Override
    public void sendMessage(BaseComponent message) {
        Message.getAudienceProvider().player(((Player) sender).getUniqueId()).sendMessage(BungeeComponentSerializer.get().deserialize(Collections.singletonList(message).toArray(new BaseComponent[0])));
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
