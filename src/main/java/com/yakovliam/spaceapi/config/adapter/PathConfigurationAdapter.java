package com.yakovliam.spaceapi.config.adapter;

import java.util.List;
import java.util.Set;

public class PathConfigurationAdapter implements ConfigurationAdapter {

    /**
     * The parent configuration adapter
     */
    private ConfigurationAdapter parent;

    /**
     * The path up until the current section
     */
    private String prefix;

    public PathConfigurationAdapter(ConfigurationAdapter parent, String prefix) {
        this.parent = parent;
        this.prefix = prefix;
    }

    @Override
    public String getString(String path, String def) {
        return parent.getString(prefix + "." + path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return parent.getInteger(prefix + "." + path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return parent.getBoolean(prefix + "." + path, def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        return parent.getStringList(prefix + "." + path, def);
    }

    @Override
    public Set<String> getKeys(String path, Set<String> def) {
        return parent.getKeys(prefix + "." + path, def);
    }

    @Override
    public Double getDouble(String path, double def) {
        return parent.getDouble(prefix + "." + path, def);
    }

    @Override
    public Long getLong(String path, Long def) {
        return parent.getLong(prefix + "." + path, def);
    }

    @Override
    public PathConfigurationAdapter getSection(String path) {
        return parent.getSection(prefix + "." + path);
    }

    /**
     * Gets the parent configuration adapter
     *
     * @return The parent
     */
    public ConfigurationAdapter getParent() {
        return parent;
    }

}
