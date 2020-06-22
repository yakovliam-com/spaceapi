package com.yakovliam.spaceapi.confignew.keys;

import com.yakovliam.spaceapi.confignew.impl.Configuration;

public interface ConfigKey<T> {

    // position in enum
    int ordinal();

    // returns the actual value
    T get(Configuration adapter);
}
