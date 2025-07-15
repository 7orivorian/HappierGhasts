package dev.tori.happierghasts.mixin;

import dev.tori.happierghasts.HappyGhastHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
@Mixin(HappyGhastEntity.class)
public abstract class MixinHappyGhastEntity extends AnimalEntity {

    @Unique
    private Entity lastPassenger = null;

    protected MixinHappyGhastEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author <a href="https://github.com/7orivorian">7orivorian</a>
     * @reason fuck you
     */
    @Overwrite
    public void initGoals() {
        HappyGhastEntity _this = (HappyGhastEntity) (Object) this;

        HappyGhastHooks.initGoals(_this, this.goalSelector, () -> this.lastPassenger);
    }

    @Inject(
            method = "removePassenger(Lnet/minecraft/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/HappyGhastEntity;clearPositionTarget()V",
                    shift = At.Shift.AFTER
            )
    )
    public void afterRemovePassenger(Entity entity, CallbackInfo ci) {
        this.lastPassenger = entity;
    }

    @Inject(
            method = "getControlledMovementInput(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    public void afterGetControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfoReturnable<Vec3d> cir) {
        HappyGhastEntity _this = (HappyGhastEntity) (Object) this;
        cir.setReturnValue(HappyGhastHooks.scaleMovement(_this, controllingPlayer, cir.getReturnValue()));
    }
}