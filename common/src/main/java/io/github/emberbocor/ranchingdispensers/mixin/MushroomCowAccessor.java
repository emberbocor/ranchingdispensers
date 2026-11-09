package io.github.emberbocor.ranchingdispensers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.item.component.SuspiciousStewEffects;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {
    @Accessor("stewEffects")
    SuspiciousStewEffects ranchingdispensers$getStewEffects();

    @Accessor("stewEffects")
    void ranchingdispensers$setStewEffects(SuspiciousStewEffects effects);
}
