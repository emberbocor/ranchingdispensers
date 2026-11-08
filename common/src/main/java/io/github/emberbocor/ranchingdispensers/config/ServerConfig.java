package io.github.emberbocor.ranchingdispensers.config;

import java.nio.file.Path;

import io.github.emberbocor.ranchingdispensers.platform.Services;

public final class ServerConfig {
    static final Settings DEFAULTS = new Settings(true, true, true, true, true);

    private static volatile Settings settings = DEFAULTS;

    private ServerConfig() {
    }

    public static void load() {
        settings = DEFAULTS;
        Path file = Services.PLATFORM.getConfigDirectory().resolve(ServerConfigFile.FILE_NAME);
        settings = new ServerConfigReader(file).read(ServerConfigFile.load(file).orElse(null));
    }

    public static Settings settings() {
        return settings;
    }

    public record Settings(boolean enabled, boolean feedAdults, boolean feedBabies,
                           boolean milkAnimals, boolean collectStew) {
    }
}
