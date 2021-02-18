package dev.spaceseries.spaceapi.abstraction.plugin;

import dev.spaceseries.spaceapi.command.BukkitSpaceCommandSender;
import dev.spaceseries.spaceapi.command.Command;
import dev.spaceseries.spaceapi.text.Message;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;

public class BukkitPlugin extends Plugin<JavaPlugin> {

    /**
     * Plugin
     */
    private final JavaPlugin plugin;

    /**
     * Construct bukkit plugin
     *
     * @param plugin plugin
     */
    public BukkitPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        AudienceProvider audienceProvider = BukkitAudiences.create(plugin);

        // initialize messages
        Message.initAudience(audienceProvider);
    }

    /**
     * Register command
     * <p>
     * Usually internal use only
     *
     * @param command command
     */
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

    /**
     * Get data folder
     *
     * @return folder
     */
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
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
            return command.execute(new BukkitSpaceCommandSender(sender), commandLabel, args);
        }
    }

    /**
     * Returns main class instance
     *
     * @return instance
     */
    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
