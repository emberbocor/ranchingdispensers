package io.github.emberbocor.ranchingdispensers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.animal.Animal;

@Mixin(Animal.class)
public interface AnimalAccessor {
    @Accessor("loveCause")
    void ranchingdispensers$setLoveCause(EntityReference<ServerPlayer> cause);
}
