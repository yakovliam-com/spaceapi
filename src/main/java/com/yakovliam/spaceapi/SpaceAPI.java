package com.yakovliam.spaceapi;

import com.yakovliam.spaceapi.gui.listener.GuiListener;
import com.yakovliam.spaceapi.listener.ChatListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

public class SpaceAPI {

    @Getter
    @Setter
    private static SpaceAPI instance;

    @Getter
    @Setter
    private Context context;

    @Getter
    @Setter
    private static Plugin bukkitContextPlugin;

    @Getter
    @Setter
    private static net.md_5.bungee.api.plugin.Plugin bungeeContextPlugin;

    public SpaceAPI(Plugin bukkitPlugin) {
        // if already initialized, return (we don't want to RE-initialize)
        if (instance != null && getBukkitContextPlugin() != null) return;

        // set instance
        setInstance(this);

        // set context
        this.context = Context.BUKKIT;

        // register Bukkit listeners
        bukkitPlugin.getServer().getPluginManager().registerEvents(new ChatListener(), bukkitPlugin);
        bukkitPlugin.getServer().getPluginManager().registerEvents(new GuiListener(), bukkitPlugin);
    }

    public SpaceAPI(net.md_5.bungee.api.plugin.Plugin bungeePlugin) {
        // if already initialized, return (we don't want to RE-initialize)
        if (instance != null && getBungeeContextPlugin() != null) return;

        // set instance
        setInstance(this);

        // set context
        this.context = Context.BUNGEECORD;
    }


    public static SpaceAPI getInstance(Plugin bukkitPlugin) {
        if (instance != null) return instance;
        return new SpaceAPI(bukkitPlugin);
    }


    public static SpaceAPI getInstance(net.md_5.bungee.api.plugin.Plugin bungeePlugin) {
        if (instance != null) return instance;
        return new SpaceAPI(bungeePlugin);
    }
}
