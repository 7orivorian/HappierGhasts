package dev.tori.happierghasts.goals;

import dev.tori.happierghasts.HappierGhasts;
import dev.tori.happierghasts.util.Maths;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Supplier;

import static dev.tori.happierghasts.HappierGhasts.CONFIG;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappyGhastRoamAroundPlayerGoal extends Goal {

    private static final int ROAM_ATTEMPTS = 64;
    private static final int MAX_FOLLOW_DISTANCE_SQ = 16_384; // 128 blocks squared

    private final Mob ghast;
    private final Supplier<Entity> lastPassenger;

    private int refreshCooldown = 0;

    public HappyGhastRoamAroundPlayerGoal(Mob ghast, Supplier<Entity> lastPassenger) {
        this.ghast = ghast;
        this.lastPassenger = lastPassenger;

        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        refreshCooldown--;

        if (!CONFIG.roaming.enabled()) {
            return false;
        }

        if (ghast.isLeashed()) {
            HappierGhasts.LOGGER.debug("{} is leashed, cannot roam around player", ghast);
            return false;
        }

        Player player = getLastPlayerPassenger();

        // Only follow living, non-spectator players
        if (player == null || player.isSpectator() || !player.isAlive()) {
            HappierGhasts.LOGGER.debug("No valid player to roam around ({})", ghast);
            return false;
        }

        // Only follow a player in the same dimension as us
        if (!player.level().dimensionType().equals(ghast.level().dimensionType())) {
            HappierGhasts.LOGGER.debug("{} is in a different dimension than player ({})", ghast, player);
            return false;
        }

        // Don't attempt to follow a player who's extremely far away
        double distanceToPlayer = ghast.distanceToSqr(player);
        if (distanceToPlayer >= MAX_FOLLOW_DISTANCE_SQ) {
            HappierGhasts.LOGGER.debug("{} is too far away to roam around player ({})", ghast, player);
            return false;
        }

        if (refreshCooldown > 0) {
            return false;
        }

        MoveControl moveControl = ghast.getMoveControl();
        if (moveControl.hasWanted()) {
            double distanceFromWanted = new Vec3(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ()).distanceToSqr(ghast.position());
            if (distanceFromWanted <= 1.0) {
                // We're close enough to the wanted position that we can choose a new one
                return true;
            }

            double distFromWantedToPlayer = new Vec3(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ()).distanceToSqr(player.position());
            int maxRoamingDistSq = CONFIG.roaming.maxDistance() * CONFIG.roaming.maxDistance();

            // Our wanted position is too far away from the player, so we should choose a new one
            if (distFromWantedToPlayer > maxRoamingDistSq) {
                HappierGhasts.LOGGER.debug("{} is outside of roaming distance! Overriding current move target... (roaming around {})", ghast, player);
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        Player player = getLastPlayerPassenger();
        if (player == null) {
            throw new IllegalStateException("getLastPlayerPassenger() is null after canUse() returned true. This should never happen.");
        }
        int maxRoamingDistance = CONFIG.roaming.maxDistance();

        // Sets our home to the player's location.
        // This keeps the Ghast in this general area if
        // a player temporarily becomes an invalid target
        ghast.setHomeTo(BlockPos.containing(player.position()), maxRoamingDistance);

        BlockPos roamingTarget = findRoamingTarget(ghast, player.position(), CONFIG.roaming.minDistance(), maxRoamingDistance, CONFIG.roaming.blockCheckDistance());

        double distFromPlayer = ghast.distanceToSqr(player);
        boolean isWithinRoamingRange = distFromPlayer < maxRoamingDistance * maxRoamingDistance;

        Vec3 wantedPos = roamingTarget.getCenter();
        double speedModifier = isWithinRoamingRange ? CONFIG.roaming.minSpeed() : CONFIG.roaming.maxSpeed();

        ghast.getMoveControl().setWantedPosition(wantedPos.x(), wantedPos.y(), wantedPos.z(), speedModifier);
        refreshCooldown = reducedTickDelay(80);
    }

    public static BlockPos findRoamingTarget(Mob ghast, Vec3 home, int minRoamDistance, int maxRoamDistance, int blockCheckDistance) {
        BlockPos fallback = BlockPos.containing(home).above(CONFIG.roaming.minDistance());
        if (ghast.distanceToSqr(home) > (maxRoamDistance * maxRoamDistance)) {
            return fallback;
        }

        Level world = ghast.level();
        RandomSource random = ghast.getRandom();

        BlockPos target;

        // Attempt to find a position to roam to
        for (int i = 0; i < ROAM_ATTEMPTS; i++) {
            Vec3 pos = Maths.randomPointWithinSphere(home, minRoamDistance, maxRoamDistance, random);
            target = BlockPos.containing(pos);

            if (target.distToCenterSqr(home) <= (maxRoamDistance * maxRoamDistance) && isBlockPosValidRoamingTarget(world, target, maxRoamDistance, blockCheckDistance)) {
                return target;
            }
        }

        // If all roam attempts fail, pick a random position
        HappierGhasts.LOGGER.warn("{} failed to find a valid target position for player roaming. Using fallback position.", ghast);
        return fallback;
    }

    private static boolean isBlockPosValidRoamingTarget(Level level, BlockPos blockPos, int maxRoamDistance, int blockCheckDistance) {
        // Block must be air
        if (!level.getBlockState(blockPos).isAir()) {
            return false;
        }
        // Must not be outside build height
        if (blockPos.getY() < level.getMinY() || blockPos.getY() > (level.getMaxY() + maxRoamDistance)) {
            return false;
        }

        if (blockCheckDistance <= 0) {
            return true;
        }

        // Ensure there's a solid block nearby
        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= blockCheckDistance; i++) {
                BlockPos offset = blockPos.relative(direction, i);
                if (level.getBlockState(offset).isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves the last passenger if it is an instance of {@link Player}.
     *
     * @return the last passenger as a {@link Player} if present and of the correct type, otherwise {@code null}.
     */
    @Nullable
    private Player getLastPlayerPassenger() {
        if (getLastPassenger() instanceof Player player) {
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