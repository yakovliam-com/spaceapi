package dev.spaceseries.spaceapi.config.old.keys;

import dev.spaceseries.spaceapi.config.old.impl.Configuration;

public interface ConfigKey<T> {

    // position in enum
    int ordinal();

    // returns the actual value
    T get(Configuration adapter);
}
