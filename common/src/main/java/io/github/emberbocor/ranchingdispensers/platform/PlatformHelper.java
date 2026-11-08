package io.github.emberbocor.ranchingdispensers.platform;

import java.nio.file.Path;

public interface PlatformHelper {
    Path getConfigDirectory();

    boolean isModLoaded(String modId);
}
