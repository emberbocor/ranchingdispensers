package io.github.emberbocor.ranchingdispensers.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import io.github.emberbocor.ranchingdispensers.RanchingDispensers;

final class ServerConfigFile {
    static final String FILE_NAME = RanchingDispensers.MODID + "-server.toml";

    private static final String DEFAULT_TEXT = """
            # Ranching Dispensers server config.
            # Created on the first server or world start.
            # Restart the server or reopen the singleplayer world to apply changes.

            # Enable all ranching actions. False restores vanilla dispensing.
            enabled = true

            # Feed ready adults for breeding; also enables optional Sheep Wool Regrowth.
            feedAdults = true

            # Feed babies with vanilla growth and whole-second rounding.
            feedBabies = true

            # Milk adult cows, mooshrooms and goats after vanilla fluid pickup fails.
            milkAnimals = true

            # Collect stew from adult mooshrooms with bowls.
            collectStew = true
            """;

    private ServerConfigFile() {
    }

    static Optional<UnmodifiableConfig> load(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.writeString(file, DEFAULT_TEXT, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                RanchingDispensers.LOGGER.info("Created {}", file);
            } catch (IOException e) {
                RanchingDispensers.LOGGER.warn("Could not create {}, using the default settings: {}", file, e.getMessage());
            }
            return parse(file, DEFAULT_TEXT);
        }

        try {
            String text = Files.readString(file, StandardCharsets.UTF_8);
            return parse(file, text.startsWith("\uFEFF") ? text.substring(1) : text);
        } catch (IOException e) {
            RanchingDispensers.LOGGER.warn("Could not read {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<UnmodifiableConfig> parse(Path file, String text) {
        try {
            return Optional.of(TomlFormat.instance().createParser().parse(text));
        } catch (RuntimeException e) {
            RanchingDispensers.LOGGER.warn("Could not parse {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }
}
