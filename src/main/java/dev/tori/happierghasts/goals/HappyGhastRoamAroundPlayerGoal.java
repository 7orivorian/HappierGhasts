package dev.tori.happierghasts.goals;

import dev.tori.happierghasts.HappierGhasts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappyGhastRoamAroundPlayerGoal extends Goal {

    private static final int ROAM_ATTEMPTS = 64;
    private static final int MAX_FOLLOW_DISTANCE_SQ = 16_384; // 128 blocks squared

    private final MobEntity ghast;
    private final Supplier<Entity> lastPassenger;
    /**
     * Minimum distance from the player to roam.
     */
    private final int minRoamDistance;
    /**
     * Maximum distance from the player to roam.
     */
    private final int maxRoamDistance;
    private final int blockCheckDistance;
    private final double minSpeed;
    private final double maxSpeed;

    public HappyGhastRoamAroundPlayerGoal(MobEntity ghast, Supplier<Entity> lastPassenger, int minRoamDistance, int maxRoamDistance, int blockCheckDistance, double minSpeed, double maxSpeed) {
        this.ghast = ghast;
        this.lastPassenger = lastPassenger;
        this.minRoamDistance = minRoamDistance;
        this.maxRoamDistance = maxRoamDistance;
        this.blockCheckDistance = blockCheckDistance;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;

        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (ghast.isLeashed()) {
            HappierGhasts.LOGGER.debug("{} is leashed, cannot roam around player", ghast);
            return false;
        }

        PlayerEntity player = getLastPlayerPassenger();

        // Only follow living, non-spectator players
        if (player == null || player.isSpectator() || !player.isAlive()) {
            HappierGhasts.LOGGER.debug("No valid player to roam around ({})", ghast);
            return false;
        }

        // Only follow a player in the same dimension as us
        if (!player.getWorld().getDimension().equals(ghast.getWorld().getDimension())) {
            HappierGhasts.LOGGER.debug("{} is in a different dimension than player ({})", ghast, player);
            return false;
        }

        // Don't attempt to follow a player who's extremely far away
        double distanceToPlayer = ghast.squaredDistanceTo(player);
        if (distanceToPlayer >= MAX_FOLLOW_DISTANCE_SQ) {
            HappierGhasts.LOGGER.debug("{} is too far away to roam around player ({})", ghast, player);
            return false;
        }

        MoveControl moveControl = ghast.getMoveControl();
        if (moveControl.isMoving()) {
            double xDiff = moveControl.getTargetX() - ghast.getX();
            double yDiff = moveControl.getTargetY() - ghast.getY();
            double zDiff = moveControl.getTargetZ() - ghast.getZ();
            double distanceFromTarget = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;

            return distanceFromTarget <= minRoamDistance
                   || distanceFromTarget >= maxRoamDistance;
        }
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void start() {
        PlayerEntity player = getLastPlayerPassenger();
        if (player == null) {
            throw new IllegalStateException("getLastPlayerPassenger() is null after canStart() returned true. This should never happen.");
        }

        Vec3d vec3d = locateTarget(ghast, player, minRoamDistance, maxRoamDistance, blockCheckDistance);

        // Sets our home to the player's location.
        // This keeps the Ghast in this general area if
        // a player temporarily becomes an invalid target
        ghast.setPositionTarget(BlockPos.ofFloored(vec3d), maxRoamDistance);

        double dist = vec3d.distanceTo(player.getPos());
        double speed = minSpeed;
        if (dist > maxRoamDistance) {
            double progress = Math.min(1.0, (dist - maxRoamDistance) / maxRoamDistance);
            speed = minSpeed + (maxSpeed - minSpeed) * progress;
        }

        ghast.getMoveControl().moveTo(vec3d.getX(), vec3d.getY(), vec3d.getZ(), speed);

        HappierGhasts.LOGGER.info("Target dist from player: {}", vec3d.distanceTo(player.getPos()));
    }

    public static Vec3d locateTarget(MobEntity ghast, Entity targetEntity, int minRoamDistance, int maxRoamDistance, int blockCheckDistance) {
        World world = ghast.getWorld();
        Random random = ghast.getRandom();
        Vec3d vec3d = ghast.getPos();
        int minRoamSq = minRoamDistance * minRoamDistance;
        int maxRoamSq = maxRoamDistance * maxRoamDistance;

        Vec3d targetEntityPos = targetEntity.getPos();

        Vec3d target = null;

        // Find a random position within min and max roam distance from the target entity
        for (int i = 0; i < ROAM_ATTEMPTS; i++) {
            target = getTargetPos(targetEntityPos, minRoamDistance, maxRoamDistance, random);
            if (!isTargetValid(world, target, blockCheckDistance)) {
                target = null;
                continue;
            }
            double distanceFromTargetEntity = target.squaredDistanceTo(targetEntityPos);
            if (distanceFromTargetEntity >= minRoamSq && distanceFromTargetEntity <= maxRoamSq) {
                return target;
            }
        }

        // If all roam attempts fail, pick a random position
        if (target == null) {
            target = addRandom(vec3d, random);
        }
        HappierGhasts.LOGGER.warn("{} failed to find a valid target position for player roaming. Using random position instead.", ghast);

        return correctVertical(ghast, world, target);
    }

    private static boolean isTargetValid(World world, Vec3d target, int blockCheckDistance) {
        if (blockCheckDistance <= 0) {
            return true;
        } else {
            BlockPos blockPos = BlockPos.ofFloored(target);
            if (world.getBlockState(blockPos).isAir()) {
                for (Direction direction : Direction.values()) {
                    for (int i = 1; i < blockCheckDistance; i++) {
                        BlockPos offset = blockPos.offset(direction, i);
                        if (!world.getBlockState(offset).isAir()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    private static Vec3d getTargetPos(Vec3d pos, double minDistance, double maxDistance, Random random) {
        return addRandom(pos, minDistance, maxDistance, random);
    }

    private static Vec3d addRandom(Vec3d pos, Random random) {
        double x = pos.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
        double y = pos.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
        double z = pos.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
        return new Vec3d(x, y, z);
    }

    private static Vec3d addRandom(Vec3d pos, double minDistance, double maxDistance, Random random) {
        // Random distance within [minDistance, maxDistance] (inclusive)
        double distance = minDistance + (random.nextDouble() * (maxDistance - minDistance));

        // Random direction (spherical coordinates)
        double theta = random.nextDouble() * 2 * Math.PI; // azimuthal angle [0, 2π]
        double phi = Math.acos(2 * random.nextDouble() - 1); // polar angle [0, π], uniformly distributed

        double xOffset = distance * Math.sin(phi) * Math.cos(theta);
        double yOffset = distance * Math.sin(phi) * Math.sin(theta);
        double zOffset = distance * Math.cos(phi);

        return new Vec3d(
                pos.getX() + xOffset,
                pos.getY() + yOffset,
                pos.getZ() + zOffset
        );
    }

    /**
     * Ensures the target position is within world height boundaries.
     *
     * @param ghast  the {@link MobEntity} for which the target position is being corrected
     * @param world  the {@link World} in which the position resides
     * @param target the initial target position to be corrected
     * @return a {@link Vec3d} representing the corrected target position
     */
    public static Vec3d correctVertical(MobEntity ghast, World world, Vec3d target) {
        BlockPos blockPos = BlockPos.ofFloored(target);
        int j = world.getTopY(Heightmap.Type.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ());
        if (j < blockPos.getY() && j > world.getBottomY()) {
            target = new Vec3d(target.getX(), ghast.getY() - Math.abs(ghast.getY() - target.getY()), target.getZ());
        }
        return target;
    }

    /**
     * Retrieves the last passenger if it is an instance of {@link PlayerEntity}.
     *
     * @return the last passenger as a {@link PlayerEntity} if present and of the correct type, otherwise {@code null}.
     */
    @Nullable
    private PlayerEntity getLastPlayerPassenger() {
        if (getLastPassenger() instanceof PlayerEntity player) {
            return player;
        }
        return null;
    }

    /**
     * Retrieves the last passenger to dismount.
     *
     * @return the last passenger entity if available, otherwise {@code null}.
     */
    @Nullable
    private Entity getLastPassenger() {
        return lastPassenger.get();
    }
}