package com.yakovliam.spaceapi.config.adapter;

import java.util.List;
import java.util.Set;

public interface ConfigurationAdapter {

    void reload();

    String getString(String path, String def);

    int getInteger(String path, int def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    Set<String> getKeys(String path, Set<String> def);

    Double getDouble(String path, double def);

    Long getLong(String path, Long def);
}
