package dev.tori.happierghasts;

import com.mojang.logging.LogUtils;
import dev.tori.happierghasts.config.HappierGhastsConfig;
import dev.tori.happierghasts.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EquipmentSlot;
import org.slf4j.Logger;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappierGhasts implements ModInitializer {

    public static final String MOD_ID = "happierghasts";
    public static final String MOD_NAME = "HappierGhasts";
    public static final HappierGhastsConfig CONFIG = HappierGhastsConfig.createAndLoad();
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final EquipmentSlot PROPELLER_SLOT = EquipmentSlot.FEET;

    @Override
    public void onInitialize() {
        ModItems.registerItemGroups();

        LOGGER.info("{} initialized!", MOD_NAME);
    }
}