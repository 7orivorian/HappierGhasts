package dev.tori.happierghasts;

import dev.tori.happierghasts.goals.HappyGhastRoamAroundPlayerGoal;
import dev.tori.happierghasts.goals.HappyGhastSwimGoal;
import dev.tori.happierghasts.goals.HappyGhastTemptGoal;
import dev.tori.happierghasts.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.Vec3d;

import java.util.function.Supplier;

import static dev.tori.happierghasts.HappierGhasts.CONFIG;
import static dev.tori.happierghasts.HappierGhasts.PROPELLER_SLOT;
import static net.minecraft.entity.passive.HappyGhastEntity.FOOD_PREDICATE;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public final class HappierGhastHooks {

    private HappierGhastHooks() {
        throw new UnsupportedOperationException("HappierGhastHooks is a utility class and cannot be instantiated");
    }

    public static void initGoals(HappyGhastEntity ghast, GoalSelector goalSelector, Supplier<Entity> lastPassenger) {
        goalSelector.add(1, new HappyGhastTemptGoal(
                ghast,
                CONFIG.temptation.speed(),
                stack -> !ghast.isWearingBodyArmor() && !ghast.isBaby() ? stack.isIn(ItemTags.HAPPY_GHAST_TEMPT_ITEMS) : FOOD_PREDICATE.test(stack),
                false,
                CONFIG.temptation.range()
        ));
        goalSelector.add(2, new HappyGhastSwimGoal(ghast));
        if (CONFIG.roaming.enabled()) {
            goalSelector.add(3, new HappyGhastRoamAroundPlayerGoal(
                    ghast,
                    lastPassenger,
                    CONFIG.roaming.minDistance(),
                    CONFIG.roaming.maxDistance(),
                    CONFIG.roaming.blockCheckDistance(),
                    CONFIG.roaming.minSpeed(),
                    CONFIG.roaming.maxSpeed()
            ));
        }
        goalSelector.add(5, new GhastEntity.FlyRandomlyGoal(ghast, 16));
    }

    public static Vec3d scaleMovement(HappyGhastEntity ghast, PlayerEntity controllingPlayer, Vec3d movementInput) {
        double multiplier = 1.0;

        ItemStack propellerStack = ghast.getEquippedStack(PROPELLER_SLOT);
        if (!propellerStack.isEmpty()) {
            if (propellerStack.isOf(ModItems.COPPER_PROPELLER)) {
                multiplier *= CONFIG.propellers.copperSpeedMultiplier();
            } else if (propellerStack.isOf(ModItems.IRON_PROPELLER)) {
                multiplier *= CONFIG.propellers.ironSpeedMultiplier();
            } else if (propellerStack.isOf(ModItems.DIAMOND_PROPELLER)) {
                multiplier *= CONFIG.propellers.diamondSpeedMultiplier();
            } else if (propellerStack.isOf(ModItems.NETHERITE_PROPELLER)) {
                multiplier *= CONFIG.propellers.netheriteSpeedMultiplier();
            }
        }
        if (isAtCruisingHeight(controllingPlayer.getEntityPos())) {
            multiplier *= CONFIG.cruising.speedMultiplier();
        }
        return movementInput.multiply(multiplier);
    }

    public static boolean isAtCruisingHeight(Vec3d pos) {
        return pos.getY() > CONFIG.cruising.activationHeight();
    }
}