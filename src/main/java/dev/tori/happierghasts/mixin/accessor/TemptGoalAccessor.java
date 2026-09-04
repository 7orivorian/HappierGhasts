package dev.tori.happierghasts.mixin.accessor;

import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
@Mixin(TemptGoal.class)
public interface TemptGoalAccessor {

    @Accessor("calmDown")
    int getCooldown();

    @Accessor("calmDown")
    void setCooldown(int cooldown);

    @Accessor("targetingConditions")
    TargetingConditions getTargetingConditions();

    @Accessor("targetingConditions")
    void setTargetingConditions(TargetingConditions targetingConditions);
}