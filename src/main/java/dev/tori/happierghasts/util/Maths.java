package dev.tori.happierghasts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class Maths {

    public static BlockPos randomOffsetWithinRadius(BlockPos center, int minRadius, int maxRadius, RandomSource random) {
        return center.offset(randomVec3iWithinBounds(minRadius, maxRadius, random));
    }

    public static Vec3i randomVec3iWithinBounds(int min, int max, RandomSource random) {
        int randomX = random.nextInt((max - min) + 1) + min;
        int randomY = random.nextInt((max - min) + 1) + min;
        int randomZ = random.nextInt((max - min) + 1) + min;

        return new Vec3i(randomX, randomY, randomZ);
    }

    /**
     * Generates a random spherical offset around a given center point.
     *
     * @param center    The origin point.
     * @param minOffset The minimum allowed distance from the center.
     * @param maxOffset The maximum allowed distance from the center.
     * @return A new {@link Vec3} representing the random position.
     */
    public static Vec3 randomPointWithinSphere(Vec3 center, double minOffset, double maxOffset, RandomSource random) {
        // Calculate a uniformly distributed radius by volume
        double r;
        if (minOffset >= maxOffset) {
            r = maxOffset;
        } else {
            double minR3 = Math.pow(minOffset, 3);
            double maxR3 = Math.pow(maxOffset, 3);

            // Cube root ensures points don't cluster heavily toward the center
            double scaledRandomR3 = minR3 + (random.nextDouble() * (maxR3 - minR3));
            r = Math.cbrt(scaledRandomR3);
        }

        // Generate random angles for the sphere
        // theta is the azimuthal angle (longitude) from 0 to 2*PI
        double theta = random.nextDouble() * 2.0 * Math.PI;

        // v acts as cos(phi), where phi is the polar angle (latitude).
        // Uniformly picking cos(phi) between -1 and 1 prevents clustering at the poles.
        double v = random.nextDouble() * 2.0 - 1.0;

        // sin(phi) is derived from cos(phi) using Pythagoras: sin^2 + cos^2 = 1
        double sinPhi = Math.sqrt(1.0 - v * v);

        // Convert spherical coordinates to Cartesian (x, y, z)
        double offsetX = r * sinPhi * Math.cos(theta);
        double offsetY = r * sinPhi * Math.sin(theta);
        double offsetZ = r * v;

        return center.add(offsetX, offsetY, offsetZ);
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}