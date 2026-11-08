package io.github.emberbocor.ranchingdispensers;

import io.github.emberbocor.ranchingdispensers.config.ServerConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(RanchingDispensers.MODID)
public class RanchingDispensersNeoForge {
    public RanchingDispensersNeoForge() {
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load());
    }
}
