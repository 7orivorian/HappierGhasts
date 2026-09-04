package dev.tori.happierghasts.mixin;

import dev.tori.happierghasts.HappierGhastHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
@Mixin(HappyGhast.class)
public abstract class MixinHappyGhastEntity extends Animal {

    @Unique
    private Entity lastPassenger = null;

    protected MixinHappyGhastEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    /**
     * @author <a href="https://github.com/7orivorian">7orivorian</a>
     * @reason because yes
     */
    @Overwrite
    public void registerGoals() {
        HappyGhast _this = (HappyGhast) (Object) this;

        HappierGhastHooks.initGoals(_this, this.goalSelector, () -> this.lastPassenger);
    }

    @Inject(
            method = "removePassenger(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/happyghast/HappyGhast;clearHome()V",
                    shift = At.Shift.AFTER
            )
    )
    public void afterRemovePassenger(Entity entity, CallbackInfo ci) {
        this.lastPassenger = entity;
    }

    @Inject(
            method = "getRiddenInput(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    public void afterGetControlledMovementInput(Player controllingPlayer, Vec3 movementInput, CallbackInfoReturnable<Vec3> cir) {
        HappyGhast _this = (HappyGhast) (Object) this;

        cir.setReturnValue(HappierGhastHooks.scaleMovement(_this, controllingPlayer, cir.getReturnValue()));
    }
}