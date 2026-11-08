package io.github.emberbocor.ranchingdispensers.ranching;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

final class ContainerOutput {
    private ContainerOutput() {
    }

    static ItemStack consume(BlockSource source, ItemStack input, ItemStack output) {
        input.consume(1, null);
        if (input.isEmpty()) {
            return output;
        }
        ItemStack remainder = source.blockEntity().insertItem(output);
        if (!remainder.isEmpty()) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            // The outer dispense call supplies the pulse's sound and animation once.
            DefaultDispenseItemBehavior.spawnItem(source.level(), remainder, 6, facing, DispenserBlock.getDispensePosition(source));
        }
        return input;
    }
}
