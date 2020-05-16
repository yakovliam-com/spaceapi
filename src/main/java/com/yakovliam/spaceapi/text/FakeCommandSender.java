package com.yakovliam.spaceapi.text;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FakeCommandSender implements CommandSender {

    private final CommandSender real;

    public FakeCommandSender(CommandSender real) {
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
    public Server getServer() {
        return real.getServer();
    }

    @Override
    public String getName() {
        return real.getName();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return real.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return real.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return real.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return real.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        throw new UnsupportedOperationException("fake sender");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new UnsupportedOperationException("fake sender");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        throw new UnsupportedOperationException("fake sender");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        throw new UnsupportedOperationException("fake sender");
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        throw new UnsupportedOperationException("fake sender");
    }

    @Override
    public void recalculatePermissions() {
        real.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return real.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return real.isOp();
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("fake sender");
    }
}
