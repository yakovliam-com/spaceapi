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

    public SpaceAPI(Plugin plugin) {
        bukkitContextPlugin = plugin;

        // register bukkit listeners
    }

    public SpaceAPI(net.md_5.bungee.api.plugin.Plugin plugin) {
        bungeeContextPlugin = plugin;
    }
}
