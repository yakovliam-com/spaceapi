package dev.spaceseries.spaceapi.abstraction.plugin;

import dev.spaceseries.spaceapi.command.BungeeSpaceCommandSender;
import dev.spaceseries.spaceapi.command.Permissible;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;

public class BungeePlugin extends Plugin<net.md_5.bungee.api.plugin.Plugin> {

    /**
     * Bungee plugin
     */
    private final net.md_5.bungee.api.plugin.Plugin plugin;

    /**
     * Construct bungee plugin
     *
     * @param plugin plugin
     */
    public BungeePlugin(net.md_5.bungee.api.plugin.Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register command
     * <p>
     * Usually internal use only
     *
     * @param command command
     */
    @Override
    public void registerCommand(dev.spaceseries.spaceapi.command.Command command) {
        Permissible permissible = command.getClass().getAnnotation(Permissible.class);
        String permission;
        if (permissible != null && permissible.value().length > 0) {
            permission = permissible.value()[0]; // bungee supports one permission only
        } else {
            permission = null;
        }
        plugin.getProxy().getPluginManager().registerCommand(plugin, new BungeeCommand(command, permission));
    }

    /**
     * Returns data folder
     *
     * @return folder
     */
    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    public static class BungeeCommand extends Command {

        private final dev.spaceseries.spaceapi.command.Command command;

        protected BungeeCommand(dev.spaceseries.spaceapi.command.Command command, String permission) {
            super(command.getName(), permission, command.getAliases().toArray(new String[]{}));
            this.command = command;
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
            command.execute(new BungeeSpaceCommandSender(sender), command.getName(), args); // label not quite accurate (but who cares)
        }
    }

    /**
     * Returns main class instance
     *
     * @return instance
     */
    @Override
    public net.md_5.bungee.api.plugin.Plugin getPlugin() {
        return plugin;
    }
}
