package com.yakovliam.spaceapi.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class BungeeConfig implements Config {

    private Configuration config;
    private File configFile;
    private String fileName;

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

    public Configuration getConfig() {
        return this.config;
    }

    @Override
    public File getConfigFile() {
        return this.configFile;
    }

    @Override
    public void load() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public Object get(String key) {
        return config.get(key);
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
