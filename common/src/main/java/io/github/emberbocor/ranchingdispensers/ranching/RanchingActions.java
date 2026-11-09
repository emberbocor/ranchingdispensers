package io.github.emberbocor.ranchingdispensers.ranching;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Predicate;

import io.github.emberbocor.ranchingdispensers.config.ServerConfig;
import io.github.emberbocor.ranchingdispensers.mixin.AnimalAccessor;
import io.github.emberbocor.ranchingdispensers.mixin.DispenserBlockAccessor;
import io.github.emberbocor.ranchingdispensers.mixin.MushroomCowAccessor;
import io.github.emberbocor.ranchingdispensers.platform.Services;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public final class RanchingActions {
    private static final Ingredient SUPPORTED_FOOD = Ingredient.of(Items.WHEAT, Items.CARROT, Items.POTATO,
            Items.BEETROOT, Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS,
            Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD, Items.GOLDEN_CARROT, Items.DANDELION);

    private RanchingActions() {
    }

    /** Returns null when the original dispenser behavior should continue. */
    public static ItemStack dispense(BlockSource source, ItemStack stack, DefaultDispenseItemBehavior behavior) {
        ServerConfig.Settings settings = ServerConfig.settings();
        if (!settings.enabled() || !source.getBlockState().is(Blocks.DISPENSER)) {
            return null;
        }
        Map<Item, DispenseItemBehavior> behaviors = DispenserBlockAccessor.ranchingdispensers$getBehaviors();
        if (stack.is(Items.BUCKET)) {
            // Vanilla's bucket handler calls super.execute only after fluid pickup fails.
            if (settings.milkAnimals() && behavior == behaviors.get(Items.BUCKET)) {
                return milk(source, stack);
            }
            return null;
        }
        if (behavior != ((Object2ObjectMap<Item, DispenseItemBehavior>) behaviors).defaultReturnValue()) {
            return null;
        }
        if (stack.is(Items.BOWL) && settings.collectStew()) {
            return stew(source, stack);
        }
        boolean woolRegrowth = settings.feedAdults() && stack.is(Items.WHEAT)
                && Services.PLATFORM.isModLoaded("sheepwoolregrowth");
        if ((settings.feedAdults() || settings.feedBabies()) && (isSupportedFood(stack) || woolRegrowth)) {
            return feed(source, stack, settings, woolRegrowth);
        }
        return null;
    }

    private static boolean isSupportedFood(ItemStack stack) {
        return SUPPORTED_FOOD.test(stack);
    }

    private static ItemStack feed(BlockSource source, ItemStack stack, ServerConfig.Settings settings, boolean woolRegrowth) {
        Animal animal = nearest(source, candidate -> canRegrow(candidate, woolRegrowth)
                || candidate.isFood(stack) && (candidate.isBaby() ? settings.feedBabies()
                : settings.feedAdults() && candidate.getAge() == 0 && candidate.canFallInLove()));
        if (animal == null) {
            return stack;
        }
        stack.shrink(1);
        if (canRegrow(animal, woolRegrowth)) {
            Sheep sheep = (Sheep) animal;
            sheep.ate();
            source.getLevel().broadcastEntityEvent(sheep, (byte) 10);
            sheep.playSound(SoundEvents.GENERIC_EAT);
        } else if (animal.isBaby()) {
            animal.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-animal.getAge()), true);
            // forcedAgeTimer is client-local; use a vanilla particle packet for automation.
            source.getLevel().sendParticles(ParticleTypes.HAPPY_VILLAGER, animal.getRandomX(1.0),
                    animal.getRandomY() + 0.5, animal.getRandomZ(1.0), 1, 0.0, 0.0, 0.0, 0.0);
            playEatingSound(animal, stack);
        } else {
            ((AnimalAccessor) animal).ranchingdispensers$setLoveCause(null);
            animal.setInLove(null);
            playEatingSound(animal, stack);
        }
        return stack;
    }

    private static boolean canRegrow(Animal animal, boolean woolRegrowth) {
        return woolRegrowth && animal instanceof Sheep sheep && !sheep.isBaby() && sheep.isSheared();
    }

    private static void playEatingSound(Animal animal, ItemStack stack) {
        if (animal instanceof Goat) {
            animal.playSound(animal.getEatingSound(stack), 1.0F, 0.8F + animal.getRandom().nextFloat() * 0.4F);
        }
    }

    private static ItemStack milk(BlockSource source, ItemStack stack) {
        Animal animal = nearest(source, candidate -> !candidate.isBaby()
                && (candidate.getType() == EntityType.COW || candidate.getType() == EntityType.MOOSHROOM
                || candidate.getType() == EntityType.GOAT));
        if (animal == null) {
            return stack;
        }
        if (animal instanceof Goat goat) {
            goat.playSound(goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_MILK : SoundEvents.GOAT_MILK, 1.0F, 1.0F);
        } else {
            animal.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
        }
        return ContainerOutput.consume(source, stack, new ItemStack(Items.MILK_BUCKET));
    }

    private static ItemStack stew(BlockSource source, ItemStack stack) {
        Animal animal = nearest(source, candidate -> !candidate.isBaby() && candidate.getType() == EntityType.MOOSHROOM);
        if (animal == null) {
            return stack;
        }
        MushroomCowAccessor mooshroom = (MushroomCowAccessor) animal;
        MobEffect effect = mooshroom.ranchingdispensers$getEffect();
        ItemStack output;
        if (effect != null) {
            output = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.saveMobEffect(output, effect, mooshroom.ranchingdispensers$getEffectDuration());
            mooshroom.ranchingdispensers$setEffect(null);
            mooshroom.ranchingdispensers$setEffectDuration(0);
            animal.playSound(SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY, 1.0F, 1.0F);
        } else {
            output = new ItemStack(Items.MUSHROOM_STEW);
            animal.playSound(SoundEvents.MOOSHROOM_MILK, 1.0F, 1.0F);
        }
        return ContainerOutput.consume(source, stack, output);
    }

    private static Animal nearest(BlockSource source, Predicate<Animal> eligible) {
        AABB box = new AABB(source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING)));
        return source.getLevel().getEntitiesOfClass(Animal.class, box,
                        animal -> animal.isAlive() && supported(animal) && eligible.test(animal))
                .stream().min(Comparator.comparingDouble((Animal animal) -> animal.distanceToSqr(source.x(), source.y(), source.z()))
                        .thenComparingInt(Animal::getId)).orElse(null);
    }

    private static boolean supported(Animal animal) {
        EntityType<?> type = animal.getType();
        return type == EntityType.COW || type == EntityType.MOOSHROOM || type == EntityType.SHEEP
                || type == EntityType.PIG || type == EntityType.CHICKEN || type == EntityType.RABBIT || type == EntityType.GOAT;
    }
}
