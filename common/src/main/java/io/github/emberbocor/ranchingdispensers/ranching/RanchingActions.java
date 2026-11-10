package io.github.emberbocor.ranchingdispensers.ranching;

import java.util.Comparator;
import java.util.function.Predicate;

import io.github.emberbocor.ranchingdispensers.config.ServerConfig;
import io.github.emberbocor.ranchingdispensers.mixin.AnimalAccessor;
import io.github.emberbocor.ranchingdispensers.mixin.DispenserBlockAccessor;
import io.github.emberbocor.ranchingdispensers.mixin.MushroomCowAccessor;
import io.github.emberbocor.ranchingdispensers.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public final class RanchingActions {
    private RanchingActions() {
    }

    /** Returns null when the original dispenser behavior should continue. */
    public static ItemStack dispense(BlockSource source, ItemStack stack, DefaultDispenseItemBehavior behavior) {
        ServerConfig.Settings settings = ServerConfig.settings();
        if (!settings.enabled() || !source.state().is(Blocks.DISPENSER)) {
            return null;
        }
        if (stack.is(Items.BUCKET)) {
            // Vanilla's bucket handler calls super.execute only after fluid pickup fails.
            if (settings.milkAnimals() && behavior == DispenserBlock.DISPENSER_REGISTRY.get(Items.BUCKET)) {
                return milk(source, stack);
            }
            return null;
        }
        if (behavior != DispenserBlockAccessor.ranchingdispensers$getDefaultBehavior()) {
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
        return stack.is(ItemTags.COW_FOOD) || stack.is(ItemTags.SHEEP_FOOD) || stack.is(ItemTags.PIG_FOOD)
                || stack.is(ItemTags.CHICKEN_FOOD) || stack.is(ItemTags.RABBIT_FOOD) || stack.is(ItemTags.GOAT_FOOD);
    }

    private static ItemStack feed(BlockSource source, ItemStack stack, ServerConfig.Settings settings, boolean woolRegrowth) {
        Animal animal = nearest(source, candidate -> canRegrow(candidate, woolRegrowth)
                || candidate.isFood(stack) && (candidate.isBaby() ? settings.feedBabies() && candidate.canAgeUp()
                : settings.feedAdults() && candidate.getAge() == 0 && candidate.canFallInLove()));
        if (animal == null) {
            return stack;
        }
        stack.consume(1, null);
        if (canRegrow(animal, woolRegrowth)) {
            Sheep sheep = (Sheep) animal;
            sheep.ate();
            source.level().broadcastEntityEvent(sheep, (byte) 10);
            sheep.playSound(SoundEvents.GENERIC_EAT.value());
        } else if (animal.isBaby()) {
            animal.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-animal.getAge()), true);
            // forcedAgeTimer is client-local; use a vanilla particle packet for automation.
            source.level().sendParticles(ParticleTypes.HAPPY_VILLAGER, animal.getRandomX(1.0),
                    animal.getRandomY() + 0.5, animal.getRandomZ(1.0), 1, 0.0, 0.0, 0.0, 0.0);
            playEatingSound(animal);
        } else {
            ((AnimalAccessor) animal).ranchingdispensers$setLoveCause(null);
            animal.setInLove(null);
            playEatingSound(animal);
        }
        return stack;
    }

    private static boolean canRegrow(Animal animal, boolean woolRegrowth) {
        return woolRegrowth && animal instanceof Sheep sheep && !sheep.isBaby() && sheep.isSheared();
    }

    private static void playEatingSound(Animal animal) {
        if (animal instanceof Goat goat) {
            animal.playSound(goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_EAT : SoundEvents.GOAT_EAT,
                    1.0F, 0.8F + animal.getRandom().nextFloat() * 0.4F);
        }
    }

    private static ItemStack milk(BlockSource source, ItemStack stack) {
        Animal animal = nearest(source, candidate -> !candidate.isBaby()
                && (candidate.getType() == EntityTypes.COW || candidate.getType() == EntityTypes.MOOSHROOM
                || candidate.getType() == EntityTypes.GOAT));
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
        Animal animal = nearest(source, candidate -> !candidate.isBaby() && candidate.getType() == EntityTypes.MOOSHROOM);
        if (animal == null) {
            return stack;
        }
        MushroomCowAccessor mooshroom = (MushroomCowAccessor) animal;
        SuspiciousStewEffects effects = mooshroom.ranchingdispensers$getStewEffects();
        ItemStack output;
        if (effects != null) {
            output = new ItemStack(Items.SUSPICIOUS_STEW);
            output.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, effects);
            mooshroom.ranchingdispensers$setStewEffects(null);
            animal.playSound(SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY, 1.0F, 1.0F);
        } else {
            output = new ItemStack(Items.MUSHROOM_STEW);
            animal.playSound(SoundEvents.MOOSHROOM_MILK, 1.0F, 1.0F);
        }
        return ContainerOutput.consume(source, stack, output);
    }

    private static Animal nearest(BlockSource source, Predicate<Animal> eligible) {
        AABB box = new AABB(source.pos().relative(source.state().getValue(DispenserBlock.FACING)));
        return source.level().getEntitiesOfClass(Animal.class, box,
                        animal -> animal.isAlive() && supported(animal) && eligible.test(animal))
                .stream().min(Comparator.comparingDouble((Animal animal) -> animal.distanceToSqr(source.center()))
                        .thenComparingInt(Animal::getId)).orElse(null);
    }

    private static boolean supported(Animal animal) {
        EntityType<?> type = animal.getType();
        return type == EntityTypes.COW || type == EntityTypes.MOOSHROOM || type == EntityTypes.SHEEP
                || type == EntityTypes.PIG || type == EntityTypes.CHICKEN || type == EntityTypes.RABBIT || type == EntityTypes.GOAT;
    }
}
