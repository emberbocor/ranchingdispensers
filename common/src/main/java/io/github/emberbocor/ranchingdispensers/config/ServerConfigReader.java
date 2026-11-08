package io.github.emberbocor.ranchingdispensers.config;

import java.nio.file.Path;
import java.util.Set;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import io.github.emberbocor.ranchingdispensers.RanchingDispensers;

final class ServerConfigReader {
    private static final Set<String> KEYS = Set.of("enabled", "feedAdults", "feedBabies", "milkAnimals", "collectStew");

    private final Path file;

    ServerConfigReader(Path file) {
        this.file = file;
    }

    ServerConfig.Settings read(UnmodifiableConfig config) {
        ServerConfig.Settings defaults = ServerConfig.DEFAULTS;
        if (config == null) {
            return defaults;
        }
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            if (!KEYS.contains(entry.getKey())) {
                RanchingDispensers.LOGGER.warn("{}: unknown option '{}', ignored", file, entry.getKey());
            }
        }
        return new ServerConfig.Settings(value(config, "enabled", defaults.enabled()),
                value(config, "feedAdults", defaults.feedAdults()), value(config, "feedBabies", defaults.feedBabies()),
                value(config, "milkAnimals", defaults.milkAnimals()), value(config, "collectStew", defaults.collectStew()));
    }

    private boolean value(UnmodifiableConfig config, String key, boolean fallback) {
        Object value = config.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean flag) {
            return flag;
        }
        RanchingDispensers.LOGGER.warn("{}: {} must be true or false, got '{}'; using {}", file, key, value, fallback);
        return fallback;
    }
}
