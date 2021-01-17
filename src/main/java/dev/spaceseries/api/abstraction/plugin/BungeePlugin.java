package dev.spaceseries.api.abstraction.plugin;

import dev.spaceseries.api.command.BungeeSpaceCommandSender;
import dev.spaceseries.api.command.Permissible;
import dev.spaceseries.api.text.Message;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;

public class BungeePlugin extends Plugin {

    private final net.md_5.bungee.api.plugin.Plugin plugin;

    public BungeePlugin(net.md_5.bungee.api.plugin.Plugin plugin) {
        this.plugin = plugin;
        AudienceProvider audienceProvider = BungeeAudiences.create(plugin);

        // initialize messages
        Message.initAudience(audienceProvider);
    }

    @Override
    public void registerCommand(dev.spaceseries.api.command.Command command) {
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

        private final dev.spaceseries.api.command.Command command;

        protected BungeeCommand(dev.spaceseries.api.command.Command command, String permission) {
            super(command.getName(), permission, command.getAliases().toArray(new String[]{}));
            this.command = command;
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
            command.execute(new BungeeSpaceCommandSender(sender), command.getName(), args); // label not quite accurate (but who cares)
        }
    }
}
