package dev.spaceseries.spaceapi.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permissible {
    String[] value();
}
