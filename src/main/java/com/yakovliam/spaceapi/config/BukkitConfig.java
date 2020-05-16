package com.yakovliam.spaceapi.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

//TODO javadoc
public class BukkitConfig implements Config {

    private FileConfiguration config;
    private File configFile;
    private File directory;
    private String fileName;
    private Plugin plugin;

    public BukkitConfig(Plugin plugin, String fileName) {
        // call other constructor with dataFolder as directory
        this(plugin, plugin.getDataFolder(), fileName);
    }

    public BukkitConfig(Plugin plugin, File directory, String fileName) {
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

    public FileConfiguration getConfig() {
        return this.config;
    }

    @Override
    public File getConfigFile() {
        return this.configFile;
    }

    @Override
    public void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public void set(String key, Object value) {
        config.set(key, value);

        try {
            config.save(configFile);
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
