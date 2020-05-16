package com.yakovliam.spaceapi.config;

import java.io.File;

public interface Config {

    File getConfigFile();

    void load();

    void set(String key, Object value);

    Object get(String key);

    String getFileName();
}
