package io.github.emberbocor.ranchingdispensers.platform;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;

public final class ForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.isLoaded(modId);
    }
}
