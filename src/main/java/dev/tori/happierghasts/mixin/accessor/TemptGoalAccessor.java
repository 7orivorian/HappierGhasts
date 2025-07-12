package dev.tori.happierghasts.mixin.accessor;

import net.minecraft.entity.ai.goal.TemptGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
@Mixin(TemptGoal.class)
public interface TemptGoalAccessor {

    @Accessor("cooldown")
    void setCooldown(int cooldown);
}