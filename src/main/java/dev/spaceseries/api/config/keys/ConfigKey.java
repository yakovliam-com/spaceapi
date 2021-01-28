package dev.spaceseries.api.config.keys;

import dev.spaceseries.api.config.impl.Configuration;

public interface ConfigKey<T> {

    // position in enum
    int ordinal();

    // returns the actual value
    T get(Configuration adapter);
}
