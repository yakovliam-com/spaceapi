package com.yakovliam.spaceapi.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class BungeeConfig implements Config {

    /**
     * The configuration object
     */
    private Configuration config;

    /**
     * The configuration file where the configuration lives
     */
    private File configFile;

    /**
     * The name of the configuration file (usually "config.yml")
     */
    private String fileName;

    /**
     * Initialize new config
     *
     * @param plugin The contextual plugin; BungeeCord
     * @param fileName The name of the file as it appears in the jar resources directory
     */
    public BungeeConfig(Plugin plugin, String fileName) {
        // load file
        configFile = new File(plugin.getDataFolder(), fileName);

        // if nonexistent, create it
        if (!configFile.exists()) {
            InputStream initialStream = plugin.getResourceAsStream(fileName);

            try {
                byte[] buffer = new byte[initialStream.available()];

                initialStream.read(buffer);

                OutputStream outStream = new FileOutputStream(configFile);
                outStream.write(buffer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // init config with file
        load();
    }

    /**
     * Gets the configuration object
     *
     * @return The configuration object
     */
    public Configuration getConfig() {
        return this.config;
    }

    /**
     * Gets the config file object
     *
     * @return The configuration file object
     */
    @Override
    public File getConfigFile() {
        return this.configFile;
    }

    /**
     * Loads the configuration into an accessible configuration object
     */
    @Override
    public void load() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a value in the configuration, automatically saves
     *
     * @param key The path of the value
     * @param value The value
     */
    @Override
    public void set(String key, Object value) {
        config.set(key, value);

        try {
            // save
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a value from a path
     *
     * @param key The path at which the value is
     * @return The value
     */
    @Override
    public Object get(String key) {
        return config.get(key);
    }

    /**
     * Gets the file name
     *
     * @return The file name
     */
    @Override
    public String getFileName() {
        return fileName;
    }
}
