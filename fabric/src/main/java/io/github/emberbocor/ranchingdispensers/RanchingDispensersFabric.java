package io.github.emberbocor.ranchingdispensers;

import io.github.emberbocor.ranchingdispensers.config.ServerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class RanchingDispensersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load());
    }
}
