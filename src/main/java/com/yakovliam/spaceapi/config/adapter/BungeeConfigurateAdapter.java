package com.yakovliam.spaceapi.config.adapter;

import com.yakovliam.spaceapi.config.BungeeConfig;

import java.util.*;

public class BungeeConfigurateAdapter implements ConfigurationAdapter {

    private BungeeConfig bungeeConfig;

    public BungeeConfigurateAdapter(BungeeConfig bungeeConfig) {
        this.bungeeConfig = bungeeConfig;
    }

    @Override
    public void reload() {
        bungeeConfig.load();
    }

    @Override
    public String getString(String path, String def) {
        return bungeeConfig.getConfig().getString(path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return bungeeConfig.getConfig().getInt(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return bungeeConfig.getConfig().getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        return Optional.of(bungeeConfig.getConfig().getStringList(path)).orElse(def);
    }

    @Override
    public Set<String> getKeys(String path, Set<String> def) {
        return new HashSet<>(Optional.of(bungeeConfig.getConfig().getSection(path).getKeys()).orElse(def));
    }

    @Override
    public Double getDouble(String path, double def) {
        return bungeeConfig.getConfig().getDouble(path, def);
    }

    @Override
    public Long getLong(String path, Long def) {
        return bungeeConfig.getConfig().getLong(path, def);
    }
}
