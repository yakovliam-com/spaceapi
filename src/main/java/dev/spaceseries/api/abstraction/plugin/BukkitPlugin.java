package dev.spaceseries.api.abstraction.plugin;

import dev.spaceseries.api.command.BukkitSpaceCommandSender;
import dev.spaceseries.api.command.Command;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;

public class BukkitPlugin extends Plugin {

    private final JavaPlugin plugin;

    public BukkitPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        BukkitAudiences.create(plugin);
    }

    @Override
    public void registerCommand(Command command) {
        SimpleCommandMap commandMap;

        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (SimpleCommandMap) f.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        commandMap.register(command.getName(), new BukkitCommand(command));
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    public static class BukkitCommand extends org.bukkit.command.defaults.BukkitCommand {

        private final Command command;

        protected BukkitCommand(Command command) {
            super(command.getName(), command.getDescription(), command.getUsageMessage(), command.getAliases());
            this.command = command;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return command.execute(new BukkitSpaceCommandSender(sender), commandLabel, args);
        }
    }

}
