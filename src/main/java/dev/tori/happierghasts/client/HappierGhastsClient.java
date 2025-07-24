package dev.tori.happierghasts.client;

import com.mojang.logging.LogUtils;
import dev.tori.happierghasts.HappierGhasts;
import dev.tori.happierghasts.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappierGhastsClient implements ClientModInitializer {

    public static final String MOD_ID = "happierghasts";
    public static final String MOD_NAME = "HappierGhasts";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}!", MOD_NAME);

        ItemTooltipCallback.EVENT.register((stack, context, type, list) -> {
            for (Item propeller : ModItems.PROPELLERS) {
                if (stack.isOf(propeller)) {
                    list.add(Text.empty());
                    list.add(Text.translatable("item.happierghasts.propeller_effect.tooltip").formatted(Formatting.GRAY));

                    int speedBonus;
                    if (stack.isOf(ModItems.COPPER_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.copperSpeedMultiplier() * 100) - 100;
                    } else if (stack.isOf(ModItems.IRON_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.ironSpeedMultiplier() * 100) - 100;
                    } else if (stack.isOf(ModItems.DIAMOND_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.diamondSpeedMultiplier() * 100) - 100;
                    } else if (stack.isOf(ModItems.NETHERITE_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.netheriteSpeedMultiplier() * 100) - 100;
                    } else {
                        speedBonus = 0;
                    }
                    MutableText text = Text.literal("+%s%s ".formatted(speedBonus, "%"));
                    text.append(Text.translatable("item.happierghasts.propeller_speed.tooltip"));
                    list.add(text.formatted(Formatting.BLUE));
                    break;
                }
            }
        });
    }
}