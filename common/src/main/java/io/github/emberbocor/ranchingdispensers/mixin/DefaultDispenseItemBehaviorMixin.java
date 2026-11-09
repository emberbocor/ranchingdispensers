package io.github.emberbocor.ranchingdispensers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.ranchingdispensers.ranching.RanchingActions;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class DefaultDispenseItemBehaviorMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void ranchingdispensers$ranch(BlockSource source, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = RanchingActions.dispense(source, stack, (DefaultDispenseItemBehavior) (Object) this);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
