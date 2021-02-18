package dev.spaceseries.spaceapi.config.keys;

import dev.spaceseries.spaceapi.config.impl.Configuration;

public interface ConfigKey<T> {

    // position in enum
    int ordinal();

    // returns the actual value
    T get(Configuration adapter);
}
