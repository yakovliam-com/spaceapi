package com.yakovliam.spaceapi.config.keys;

import com.yakovliam.spaceapi.config.adapter.ConfigurationAdapter;

import java.util.List;

public class ConfigKeyTypes {

    private static final KeyFactory<Boolean> BOOLEAN = ConfigurationAdapter::getBoolean;
    private static final KeyFactory<String> STRING = ConfigurationAdapter::getString;
    private static final KeyFactory<List<String>> STRING_LIST = ConfigurationAdapter::getStringList;
    private static final KeyFactory<Integer> INTEGER = ConfigurationAdapter::getInteger;
    private static final KeyFactory<Double> DOUBLE = ConfigurationAdapter::getDouble;
    private static final KeyFactory<Long> LONG = ConfigurationAdapter::getLong;

    private static final KeyFactory<Enum<?>> ENUM = (adapter, path, def) -> {
        String s = adapter.getString(path, def.name());
        for (Enum<?> e : def.getDeclaringClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(s))
                return e;
        }
        return null;
    };

    public static BaseConfigKey<Boolean> booleanKey(String path, boolean def) {
        return BOOLEAN.createKey(path, def);
    }

    public static BaseConfigKey<String> stringKey(String path, String def) {
        return STRING.createKey(path, def);
    }

    public static BaseConfigKey<List<String>> stringListKey(String path, List<String> def) {
        return STRING_LIST.createKey(path, def);
    }

    public static BaseConfigKey<Integer> integerKey(String path, Integer def) {
        return INTEGER.createKey(path, def);
    }

    public static BaseConfigKey<Double> doubleKey(String path, double def) {
        return DOUBLE.createKey(path, def);
    }

    public static BaseConfigKey<Long> longKey(String path, long def) {
        return LONG.createKey(path, def);
    }

    public static <T extends Enum<T>> BaseConfigKey<T> enumKey(String path, T def) {
        return (BaseConfigKey<T>) ENUM.createKey(path, def);
    }

    public interface KeyFactory<T> {

        T getValue(ConfigurationAdapter adapter, String path, T def);

        default BaseConfigKey<T> createKey(String path, T def) {
            return new FunctionalKey<>(this, path, def);
        }
    }


    public abstract static class BaseConfigKey<T> implements ConfigKey<T> {
        int ordinal = -1;

        BaseConfigKey() {

        }

        @Override
        public int ordinal() {
            return this.ordinal;
        }
    }

    private static class FunctionalKey<T> extends BaseConfigKey<T> implements ConfigKey<T> {
        private final KeyFactory<T> factory;
        private final String path;
        private final T def;

        FunctionalKey(KeyFactory<T> factory, String path, T def) {
            this.factory = factory;
            this.path = path;
            this.def = def;
        }

        @Override
        public T get(ConfigurationAdapter adapter) {
            return this.factory.getValue(adapter, this.path, this.def);
        }
    }
}
