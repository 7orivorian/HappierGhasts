package dev.tori.happierghasts.goals;

import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.HappyGhastEntity;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappyGhastSwimGoal extends SwimGoal {

    private final HappyGhastEntity entity;

    public HappyGhastSwimGoal(HappyGhastEntity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return !entity.isStill() && super.canStart();
    }
}