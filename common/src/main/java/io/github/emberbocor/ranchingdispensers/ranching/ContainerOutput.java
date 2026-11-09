package io.github.emberbocor.ranchingdispensers.ranching;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

final class ContainerOutput {
    private ContainerOutput() {
    }

    static ItemStack consume(BlockSource source, ItemStack input, ItemStack output) {
        input.shrink(1);
        if (input.isEmpty()) {
            return output;
        }
        if (source.<DispenserBlockEntity>getEntity().addItem(output) < 0) {
            Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
            // The outer dispense call supplies the pulse's sound and animation once.
            DefaultDispenseItemBehavior.spawnItem(source.getLevel(), output, 6, facing, DispenserBlock.getDispensePosition(source));
        }
        return input;
    }
}
