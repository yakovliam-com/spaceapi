package com.yakovliam.spaceapi.config.adapter;

import com.yakovliam.spaceapi.config.Config;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BukkitConfigurateAdaption implements ConfigurationAdapter {

    private Config bukkitConfig;
    private Plugin plugin;

    public BukkitConfigurateAdaption(Plugin plugin, Config bukkitConfig) {
        this.plugin = plugin;
        this.bukkitConfig = bukkitConfig;
    }

    @Override
    public void reload() {
        bukkitConfig.load();
    }

    @Override
    public String getString(String path, String def) {
        return bukkitConfig.getConfig().getString(path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return bukkitConfig.getConfig().getInt(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return bukkitConfig.getConfig().getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        return Optional.of(bukkitConfig.getConfig().getStringList(path)).orElse(def);
    }

    @Override
    public Set<String> getKeys(String path, Set<String> def) {
        return Optional.of(bukkitConfig.getConfig().getConfigurationSection(path).getKeys(false)).orElse(def);
    }

    @Override
    public Double getDouble(String path, double def) {
        return bukkitConfig.getConfig().getDouble(path, def);
    }

    @Override
    public Long getLong(String path, Long def) {
        return bukkitConfig.getConfig().getLong(path, def);
    }
}
