package dev.spaceseries.spaceapi.config;

import com.google.common.collect.ImmutableList;
import dev.spaceseries.spaceapi.config.generic.KeyedConfiguration;
import dev.spaceseries.spaceapi.config.generic.key.ConfigKey;
import dev.spaceseries.spaceapi.config.generic.key.SimpleConfigKey;
import dev.spaceseries.spaceapi.config.util.ImmutableCollectors;
import dev.spaceseries.spaceapi.util.Pair;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static dev.spaceseries.spaceapi.config.generic.key.ConfigKeyFactory.*;

/**
 * All of the {@link ConfigKey}s used by Space[Plugin Name Here].
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public final class ExampleConfigKeys {
    private ExampleConfigKeys() {
    }

    /**
     * The name of the server
     */
    public static final ConfigKey<String> SERVER = key(c -> {
        String server = c.getString("server", "global").toLowerCase();
        if (server.equals("load-from-system-property")) {
            server = System.getProperty("spaceapi.server", "global").toLowerCase();
        }
        return server;
    });

    /**
     * How many minutes to wait between syncs. A value <= 0 will disable syncing.
     */
    public static final ConfigKey<Integer> SYNC_TIME = notReloadable(key(c -> {
        int val = c.getInteger("sync-minutes", -1);
        if (val == -1) {
            val = c.getInteger("data.sync-minutes", -1);
        }
        return val;
    }));

    /**
     * A set of disabled contexts
     */
    public static final ConfigKey<Set<String>> DISABLED_CONTEXTS = notReloadable(key(c -> c.getStringList("disabled-contexts", ImmutableList.of())
            .stream()
            .map(String::toLowerCase)
            .collect(ImmutableCollectors.toSet())));

    /**
     * # If the servers own UUID cache/lookup facility should be used when there is no record for a player in the X cache.
     */
    public static final ConfigKey<Boolean> USE_SERVER_UUID_CACHE = booleanKey("use-server-uuid-cache", false);

    /**
     * If X should allow usernames with non alphanumeric characters.
     */
    public static final ConfigKey<Boolean> ALLOW_INVALID_USERNAMES = booleanKey("allow-invalid-usernames", false);

    /**
     * If X should not require users to confirm bulkupdate operations.
     */
    public static final ConfigKey<Boolean> SKIP_BULKUPDATE_CONFIRMATION = booleanKey("skip-bulkupdate-confirmation", false);

    /**
     * Controls custom objects
     */
    public static final ConfigKey<Pair<String, String>> CUSTOM_OBJECT_KEY = key(c -> {
        String value = c.getString("temporary-add-behaviour", "deny");
        switch (value.toLowerCase()) {
            case "accumulate":
                return new Pair<>("ACCUMULATE", "TEST");
            case "replace":
                return new Pair<>("REPLACE", "TEST");
            default:
                return new Pair<>(null, null);
        }
    });

    /**
     * A functional config key
     */
    public static final ConfigKey<Function<String, String>> PRIMARY_GROUP_CALCULATION = notReloadable(key(c -> {
        String option = "something";
        return null;
    }));

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(ExampleConfigKeys.class);

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }

}
