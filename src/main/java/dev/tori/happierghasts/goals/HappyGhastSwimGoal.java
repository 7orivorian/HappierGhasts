package dev.tori.happierghasts.goals;

import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappyGhastSwimGoal extends FloatGoal {

    private final HappyGhast entity;

    public HappyGhastSwimGoal(HappyGhast entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return !entity.isOnStillTimeout() && super.canUse();
    }
}