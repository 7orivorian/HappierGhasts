package dev.tori.happierghasts;

import dev.tori.happierghasts.goals.HappyGhastRoamAroundPlayerGoal;
import dev.tori.happierghasts.goals.HappyGhastSwimGoal;
import dev.tori.happierghasts.goals.HappyGhastTemptGoal;
import dev.tori.happierghasts.item.ModItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

import static dev.tori.happierghasts.HappierGhasts.CONFIG;
import static dev.tori.happierghasts.HappierGhasts.PROPELLER_SLOT;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public final class HappierGhastHooks {

    private HappierGhastHooks() {
        throw new UnsupportedOperationException("HappierGhastHooks is a utility class and cannot be instantiated");
    }

    public static void initGoals(HappyGhast ghast, GoalSelector goalSelector, Supplier<Entity> lastPassenger) {
        goalSelector.addGoal(1, new HappyGhastTemptGoal.ForNonPathfinders(
                ghast,
                1.0,
                stack -> !ghast.isBaby() ? stack.is(ItemTags.HAPPY_GHAST_TEMPT_ITEMS) : stack.is(ItemTags.HAPPY_GHAST_FOOD),
                false,
                7.0
        ));
        goalSelector.addGoal(2, new HappyGhastSwimGoal(ghast));
        goalSelector.addGoal(3, new HappyGhastRoamAroundPlayerGoal(ghast, lastPassenger));
        goalSelector.addGoal(5, new Ghast.RandomFloatAroundGoal(ghast, 16));
    }

    public static Vec3 scaleMovement(HappyGhast ghast, Player controllingPlayer, Vec3 movementInput) {
        double multiplier = 1.0;

        ItemStack propellerStack = ghast.getItemBySlot(PROPELLER_SLOT);
        if (!propellerStack.isEmpty()) {
            if (propellerStack.is(ModItems.COPPER_PROPELLER)) {
                multiplier *= CONFIG.propellers.copperSpeedMultiplier();
            } else if (propellerStack.is(ModItems.IRON_PROPELLER)) {
                multiplier *= CONFIG.propellers.ironSpeedMultiplier();
            } else if (propellerStack.is(ModItems.DIAMOND_PROPELLER)) {
                multiplier *= CONFIG.propellers.diamondSpeedMultiplier();
            } else if (propellerStack.is(ModItems.NETHERITE_PROPELLER)) {
                multiplier *= CONFIG.propellers.netheriteSpeedMultiplier();
            }
        }
        if (isAtCruisingHeight(controllingPlayer.position())) {
            multiplier *= CONFIG.cruising.speedMultiplier();
        }
        return movementInput.scale(multiplier);
    }

    public static boolean isAtCruisingHeight(Vec3 pos) {
        return pos.y() > CONFIG.cruising.activationHeight();
    }
}