package dev.tori.happierghasts;

import com.mojang.logging.LogUtils;
import dev.tori.happierghasts.config.HappierGhastsConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappierGhasts implements ModInitializer {

    public static final String MOD_ID = "happierghasts";
    public static final String MOD_NAME = "HappierGhasts";
    public static final HappierGhastsConfig CONFIG = HappierGhastsConfig.createAndLoad();

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("{} initialized!", MOD_NAME);
    }
}