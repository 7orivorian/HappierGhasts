package dev.tori.happierghasts.goals;

import dev.tori.happierghasts.mixin.accessor.TemptGoalAccessor;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

import static dev.tori.happierghasts.HappierGhasts.CONFIG;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappyGhastTemptGoal extends TemptGoal {

    public HappyGhastTemptGoal(HappyGhastEntity ghast, double speed, Predicate<ItemStack> predicate, boolean canBeScared, double range) {
        super(ghast, speed, predicate, canBeScared, range);
    }

    @Override
    public void stop() {
        super.stop();
        ((TemptGoalAccessor) this).setCooldown(toGoalTicks(CONFIG.temptation.cooldownTicks()));
    }

    @Override
    protected void stopMoving() {
        mob.getMoveControl().setWaiting();
    }

    @Override
    protected void startMovingTo(PlayerEntity player) {
        Vec3d vec3d = player.getEyePos().subtract(mob.getPos()).multiply(mob.getRandom().nextDouble()).add(mob.getPos());
        mob.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, speed);
    }
}