package dev.tori.happierghasts.goals;

import dev.tori.happierghasts.mixin.accessor.TemptGoalAccessor;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

import static dev.tori.happierghasts.HappierGhasts.CONFIG;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappyGhastTemptGoal extends TemptGoal {

    public HappyGhastTemptGoal(HappyGhast ghast, double speedModifier, Predicate<ItemStack> predicate, boolean canBeScared, double range) {
        super(ghast, speedModifier, predicate, canBeScared, range);
    }

    @Override
    public boolean canUse() {
        int cooldown = ((TemptGoalAccessor) this).getCooldown();
        if (cooldown > 0) {
            ((TemptGoalAccessor) this).setCooldown(cooldown - 1);
            return false;
        } else {
            int temptationRange = CONFIG.temptation.range();
            player = getServerLevel(mob).getNearestPlayer(((TemptGoalAccessor) this).getTargetingConditions().range(temptationRange <= 0 ? mob.getAttributeValue(Attributes.TEMPT_RANGE) : temptationRange), mob);
            return player != null;
        }
    }

    @Override
    public void stop() {
        super.stop();
        ((TemptGoalAccessor) this).setCooldown(reducedTickDelay(CONFIG.temptation.cooldownTicks()));
    }

    @Override
    public void tick() {
        if (player == null) {
            return;
        }

        double stopDistance = CONFIG.temptation.range();
        mob.getLookControl().setLookAt(player, mob.getMaxHeadYRot() + 20, mob.getMaxHeadXRot());
        if (mob.distanceToSqr(player) < stopDistance * stopDistance) {
            stopNavigation();
        } else {
            navigateTowards(player);
        }
    }

    @Override
    protected void stopNavigation() {
        mob.getMoveControl().setWait();
    }

    @Override
    protected void navigateTowards(Player player) {
        Vec3 target = player.getEyePosition().subtract(mob.position()).scale(mob.getRandom().nextDouble()).add(mob.position());
        mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, CONFIG.temptation.speed());
    }

    public static class ForNonPathfinders extends TemptGoal {

        public ForNonPathfinders(PathfinderMob pathfinderMob, double speedModifier, Predicate<ItemStack> predicate, boolean canScare, double range) {
            super(pathfinderMob, speedModifier, predicate, canScare, range);
        }

        @Override
        protected void navigateTowards(Player player) {
            Vec3 target = player.getEyePosition().subtract(mob.position()).scale(mob.getRandom().nextDouble()).add(mob.position());
            mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, CONFIG.temptation.speed());
        }
    }
}