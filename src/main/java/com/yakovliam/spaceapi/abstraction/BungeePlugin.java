package com.yakovliam.spaceapi.abstraction;

import com.yakovliam.spaceapi.command.BungeeSpaceCommandSender;
import com.yakovliam.spaceapi.command.Permissible;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class BungeePlugin extends AbstractPlugin {

    private final Plugin plugin;

    public BungeePlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(com.yakovliam.spaceapi.command.Command command) {
        Permissible permissible = command.getClass().getAnnotation(Permissible.class);
        String permission;
        if (permissible != null && permissible.value().length > 0) {
            permission = permissible.value()[0]; // bungee supports one permission only
        } else {
            permission = null;
        }
        plugin.getProxy().getPluginManager().registerCommand(plugin, new BungeeCommand(command, permission));
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    public static class BungeeCommand extends Command {

        private final com.yakovliam.spaceapi.command.Command command;

        protected BungeeCommand(com.yakovliam.spaceapi.command.Command command, String permission) {
            super(command.getName(), permission, command.getAliases().toArray(new String[]{}));
            this.command = command;
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
            command.execute(new BungeeSpaceCommandSender(sender), command.getName(), args); // label not quite accurate (but who cares)
        }
    }



}
