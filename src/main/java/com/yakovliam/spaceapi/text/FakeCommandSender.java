package com.yakovliam.spaceapi.text;

import com.yakovliam.spaceapi.command.SpaceCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeCommandSender extends SpaceCommandSender {

    private final SpaceCommandSender real;

    public FakeCommandSender(SpaceCommandSender real) {
        this.real = real;
    }

    private List<String> messages = new ArrayList<>();

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public void sendMessage(String message) {
        messages.add(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public String getName() {
        return real.getName();
    }

    @Override
    public UUID getUuid() {
        return real.getUuid();
    }

    @Override
    public void sendMessage(BaseComponent message) {
        sendMessage(message.toLegacyText());
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        BaseComponent.toLegacyText(message);
    }

    @Override
    public boolean hasPermission(String name) {
        return real.hasPermission(name);
    }

}
