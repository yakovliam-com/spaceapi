package com.yakovliam.spaceapi.confignew.obj;

import com.yakovliam.spaceapi.abstraction.AbstractPlugin;
import com.yakovliam.spaceapi.confignew.impl.Configuration;
import com.yakovliam.spaceapi.confignew.impl.ConfigurationProvider;
import com.yakovliam.spaceapi.confignew.impl.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config  {

    /**
     * The configuration object
     */
    private Configuration config;

    /**
     * The configuration file object
     */
    private File configFile;

    /**
     * The directory that the config lives in (usually {Plugin#getDataFolder()}, but can be customized
     */
    private File directory;

    /**
     * The name of the config file (usually "config.yml")
     */
    private String fileName;

    /**
     * The contextual plugin
     */
    private AbstractPlugin plugin;

    /**
     * Initializes new config; directory defaults to {Plugin#getDataFolder()}
     *
     * @param plugin The contextual plugin
     * @param fileName The name of the config as it appears in the jar's resources directory
     */
    public Config(AbstractPlugin plugin, String fileName) {
        // call other constructor with dataFolder as directory
        this(plugin, plugin.getDataFolder(), fileName);
    }

    /**
     * Initializes new config
     *
     * @param plugin The contextual plugin
     * @param fileName The name of the config as it appears in the jar's resources directory
     * @param directory The custom directory to put the target config in (external, usually in the plugins/PluginName folder)
     */
    public Config(AbstractPlugin plugin, File directory, String fileName) {
        // init
        this.plugin = plugin;
        this.directory = directory;

        // save resource to directory
        if (!new File(this.directory, fileName).exists())
            saveResource(fileName, false);

        // now, create the file object for use
        configFile = new File(directory, fileName);

        // init config with file
        load();
    }

    /**
     * Implementation of Bukkit's {Plugin#saveResource()} method to accommodate for this custom configuration directory, etc
     *
     * @param resourcePath The path of the resource, usually just the name of the configuration file in the jar (e.g. "config.yml")
     * @param replace Do we want to replace the external config if it exists?
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
                Logger.getGlobal().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    /**
     * Implementation of Bukkit's {Plugin#getResource()} method to accommodate for new features present in this class
     *
     * @param filename The name of the file (path CAN be included, but not needed) in the jar's resources folder
     * @return The inputStream linked with the file in the jar's resource folder
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
     * Gets the configuration object
     *
     * @return The configuration object
     */
    public Configuration getConfig() {
        return this.config;
    }

    /**
     * Gets the configuration file object
     *
     * @return The configuration file object
     */
    public File getConfigFile() {
        return this.configFile;
    }

    /**
     * Loads the configuration into an accessible object
     */
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
    public void set(String key, Object value) {
        config.set(key, value);

        try {
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
    public Object get(String key) {
        return config.get(key);
    }

    /**
     * Gets the file name
     *
     * @return The file name
     */
    public String getFileName() {
        return fileName;
    }
}
