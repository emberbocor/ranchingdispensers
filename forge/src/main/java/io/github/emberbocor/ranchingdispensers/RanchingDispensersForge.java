package io.github.emberbocor.ranchingdispensers;

import io.github.emberbocor.ranchingdispensers.config.ServerConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(RanchingDispensers.MODID)
public class RanchingDispensersForge {
    public RanchingDispensersForge() {
        MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load());
    }
}
