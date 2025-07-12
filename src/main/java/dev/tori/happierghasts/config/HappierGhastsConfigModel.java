package dev.tori.happierghasts.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
@Modmenu(modId = "happierghasts")
@Config(name = "happier-ghasts", wrapperName = "HappierGhastsConfig")
@Sync(Option.SyncMode.OVERRIDE_CLIENT)
public class HappierGhastsConfigModel {

    @Nest
    @Expanded
    public Cruising cruising = new Cruising();

    public static class Cruising {

        public int cruisingHeight = 186;
        @RangeConstraint(min = 1.0, max = 10.0)
        public double cruisingHeightSpeedMultiplier = 2.0;
    }

    @Nest
    @Expanded
    public Roaming roaming = new Roaming();

    public static class Roaming {

        public boolean enabled = true;
        @RangeConstraint(min = 0, max = 128)
        public int minRoamDistance = 8;
        @RangeConstraint(min = 0, max = 128)
        public int maxRoamDistance = 16;
        @RangeConstraint(min = 0.1, max = 10.0)
        public double roamSpeed = 1.1;
        @RangeConstraint(min = 0, max = 64)
        public int roamBlockCheckDistance = 0;
    }

    @Nest
    @Expanded
    public Temptation temptation = new Temptation();

    public static class Temptation {

        @RangeConstraint(min = 0, max = 10)
        public int temptRange = 5;
        @RangeConstraint(min = 0.1, max = 5.0)
        public double temptSpeed = 1.1;
        @RangeConstraint(min = 0, max = 200)
        public int temptCooldownTicks = 100;
    }
}