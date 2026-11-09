package io.github.emberbocor.ranchingdispensers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.animal.MushroomCow;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {
    @Accessor("effect")
    MobEffect ranchingdispensers$getEffect();

    @Accessor("effect")
    void ranchingdispensers$setEffect(MobEffect effect);

    @Accessor("effectDuration")
    int ranchingdispensers$getEffectDuration();

    @Accessor("effectDuration")
    void ranchingdispensers$setEffectDuration(int duration);
}
