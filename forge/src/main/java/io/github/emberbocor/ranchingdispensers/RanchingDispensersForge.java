package io.github.emberbocor.ranchingdispensers;

import io.github.emberbocor.ranchingdispensers.config.ServerConfig;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(RanchingDispensers.MODID)
public class RanchingDispensersForge {
    public RanchingDispensersForge() {
        ServerAboutToStartEvent.BUS.addListener(event -> ServerConfig.load());
    }
}
