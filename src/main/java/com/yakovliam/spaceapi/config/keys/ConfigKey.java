package com.yakovliam.spaceapi.config.keys;

import com.yakovliam.spaceapi.config.impl.Configuration;

public interface ConfigKey<T> {

    // position in enum
    int ordinal();

    // returns the actual value
    T get(Configuration adapter);
}
