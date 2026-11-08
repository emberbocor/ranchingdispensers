package io.github.emberbocor.ranchingdispensers.platform;

import java.nio.file.Path;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.ModList;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
