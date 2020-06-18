package com.yakovliam.spaceapi.abstraction.plugin;

import com.yakovliam.spaceapi.command.BukkitSpaceCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class BukkitPlugin extends Plugin {

    private final JavaPlugin plugin;

    public BukkitPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(com.yakovliam.spaceapi.command.Command command) {
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

    public static class BukkitCommand extends org.bukkit.command.defaults.BukkitCommand {

        private final com.yakovliam.spaceapi.command.Command command;

        protected BukkitCommand(com.yakovliam.spaceapi.command.Command command) {
            super(command.getName(), command.getDescription(), command.getUsageMessage(), command.getAliases());
            this.command = command;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return command.execute(new BukkitSpaceCommandSender(sender), commandLabel, args);
        }
    }

}
