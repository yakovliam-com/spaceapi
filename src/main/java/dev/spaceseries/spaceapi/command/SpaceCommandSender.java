package dev.spaceseries.spaceapi.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.UUID;

public abstract class SpaceCommandSender {

    public abstract String getName();

    public abstract UUID getUuid();

    public abstract void sendMessage(BaseComponent... message);

    public abstract void sendMessage(BaseComponent message);

    public abstract boolean hasPermission(String s);

    public void sendMessage(String message) {
        sendMessage(TextComponent.fromLegacyText(message));
    }

    public void sendMessage(String... message) {
        Arrays.stream(message).map(TextComponent::fromLegacyText).forEach(this::sendMessage);
    }

    public abstract boolean isPlayer();
}
