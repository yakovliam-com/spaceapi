package com.yakovliam.spaceapi.config.keys;

import com.yakovliam.spaceapi.config.adapter.ConfigurationAdapter;

public interface ConfigKey<T> {

    // position in enum
    int ordinal();

    // returns the actual value
    T get(ConfigurationAdapter adapter);
}
