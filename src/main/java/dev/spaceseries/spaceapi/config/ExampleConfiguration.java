package dev.spaceseries.spaceapi.config;

import dev.spaceseries.spaceapi.abstraction.plugin.Plugin;
import dev.spaceseries.spaceapi.config.generic.KeyedConfiguration;
import dev.spaceseries.spaceapi.config.generic.adapter.ConfigurationAdapter;

public final class ExampleConfiguration extends KeyedConfiguration {
    private final Plugin<?> plugin;

    public ExampleConfiguration(Plugin<?> plugin, ConfigurationAdapter adapter) {
        super(adapter, null);
        this.plugin = plugin;

        init();
    }

    @Override
    protected void load(boolean initial) {
        super.load(initial);
    }

    @Override
    public void reload() {
        super.reload();
    }

    public Plugin<?> getPlugin() {
        return this.plugin;
    }
}
