package dev.tori.happierghasts;

import dev.tori.happierghasts.goals.HappyGhastRoamAroundPlayerGoal;
import dev.tori.happierghasts.goals.HappyGhastSwimGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.Vec3d;

import java.util.function.Supplier;

import static dev.tori.happierghasts.HappierGhasts.CONFIG;
import static net.minecraft.entity.passive.HappyGhastEntity.FOOD_PREDICATE;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public final class HappyGhastHooks {

    private HappyGhastHooks() {
        throw new UnsupportedOperationException("GhastUtil is a utility class and cannot be instantiated");
    }

    public static void initGoals(HappyGhastEntity ghast, GoalSelector goalSelector, Supplier<Entity> lastPassenger) {
        goalSelector.add(1, new TemptGoal.HappyGhastTemptGoal(
                ghast,
                CONFIG.temptation.temptSpeed(),
                stack -> !ghast.isWearingBodyArmor() && !ghast.isBaby() ? stack.isIn(ItemTags.HAPPY_GHAST_TEMPT_ITEMS) : FOOD_PREDICATE.test(stack),
                false,
                CONFIG.temptation.temptRange()
        ));
        goalSelector.add(2, new HappyGhastSwimGoal(ghast));
        if (CONFIG.roaming.enabled()) {
            goalSelector.add(3, new HappyGhastRoamAroundPlayerGoal(
                    ghast,
                    lastPassenger,
                    CONFIG.roaming.minRoamDistance(),
                    CONFIG.roaming.maxRoamDistance(),
                    CONFIG.roaming.roamBlockCheckDistance(),
                    CONFIG.roaming.roamSpeed()
            ));
        }
        goalSelector.add(5, new GhastEntity.FlyRandomlyGoal(ghast, 16));
    }

    public static Vec3d scaleMovement(PlayerEntity controllingPlayer, Vec3d movementInput) {
        if (isAtCruisingHeight(controllingPlayer.getPos())) {
            movementInput = movementInput.multiply(CONFIG.cruising.cruisingHeightSpeedMultiplier());
        }
        return movementInput;
    }

    public static boolean isAtCruisingHeight(Vec3d pos) {
        return pos.getY() > CONFIG.cruising.cruisingHeight();
    }
}