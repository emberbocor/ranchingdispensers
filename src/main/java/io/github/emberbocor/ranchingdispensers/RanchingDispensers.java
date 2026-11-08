package io.github.emberbocor.ranchingdispensers;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(RanchingDispensers.MODID)
public class RanchingDispensers {
    public static final String MODID = "ranchingdispensers";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RanchingDispensers(IEventBus modEventBus, ModContainer modContainer) {
    }
}
