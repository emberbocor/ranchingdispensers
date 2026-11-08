package io.github.emberbocor.ranchingdispensers.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.animal.Animal;

@Mixin(Animal.class)
public interface AnimalAccessor {
    @Accessor("loveCause")
    void ranchingdispensers$setLoveCause(UUID cause);
}
