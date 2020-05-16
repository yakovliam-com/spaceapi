package com.yakovliam.spaceapi.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class Config {

    /**
     * Configuration object from Bukkit
     */
    private FileConfiguration config;

    /**
     * The name of the config file (e.g. "config.yml")
     */
    private String fileName;

    /**
     * The file object of the file
     */
    private final File configFile;

    /**
     * The directory that the configuration file lives in
     */
    private final File directory;

    /**
     * The contextual plugin that manages the config
     */
    private final Plugin plugin;

    /**
     * Constructor that initializes a configuration object which defaults to the plugin DataFolder as the directory
     *
     * @param plugin The contextual plugin
     * @param fileName The name of the file (e.g. "config.yml")
     */
    public Config(Plugin plugin, String fileName) {
        // call other constructor with dataFolder as directory
        this(plugin, plugin.getDataFolder(), fileName);
    }

    /**
     * Constructor that initializes a configuration object which sets a custom directory
     *
     * @param plugin The contextual plugin
     * @param fileName The name of the file (e.g. "config.yml")
     * @param directory The custom directory that the target configuration file lives in
     */
    public Config(Plugin plugin, File directory, String fileName) {
        // init
        this.plugin = plugin;
        this.directory = directory;

        // save resource to directory
        saveResource(fileName, false);

        // now, create the file object for use
        configFile = new File(directory, fileName);

        // init config with file
        load();
    }

    /**
     * Implementation of {Plugin#saveResource} method which is modified to use our custom / default directory
     *
     * @param resourcePath The configuration file name as it appears in the jar (no directory along with it unless there is a directory in the jar's resource folder)
     * @param replace Will we replace the file if it exists?
     */
    public void saveResource(String resourcePath, boolean replace) {
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found!'");
        }

        File outFile = new File(directory, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(directory, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    /**
     * Implementation of Bukkit's {Plugin#getResource} method to accommodate new changes and features of SpaceAPI (regarding directories and specific settings)
     *
     * @param filename The name of the file as it appears in the jar
     * @return The inputStream that is linked to the actual file inside of the jar
     */
    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = plugin.getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Gets the Bukkit FileConfiguration object once loaded
     *
     * @return The Bukkit FileConfiguration object
     */
    public FileConfiguration getConfig() {
        return this.config;
    }

    /**
     * Gets the target file at which the config lives
     *
     * @return The config file
     */
    public File getConfigFile() {
        return this.configFile;
    }

    /**
     * Loads the configuration file into a FileConfiguration object to use
     */
    public void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Custom simple implementation that allows a plugin to set a key without explicitly saving the configuration
     *
     * @param key The path at which to set the value
     * @param value The value, which can be any object type
     */
    public void set(String key, Object value) {
        config.set(key, value);

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simple implementation of the FileConfiguration's {#get} method which returns an object
     * @param key
     * @return
     */
    public Object get(String key) {
        return config.get(key);
    }

    /**
     * Gets the name of the file at which the configuration lives, usually as it appears in the jar
     *
     * @return The file's name
     */
    public String getFileName() {
        return fileName;
    }
}
