package dev.tori.happierghasts.client;

import com.mojang.logging.LogUtils;
import dev.tori.happierghasts.HappierGhasts;
import dev.tori.happierghasts.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
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
                if (stack.is(propeller)) {
                    list.add(Component.empty());
                    list.add(Component.translatable("item.happierghasts.propeller_effect.tooltip").withStyle(ChatFormatting.GRAY));

                    int speedBonus;
                    if (stack.is(ModItems.COPPER_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.copperSpeedMultiplier() * 100) - 100;
                    } else if (stack.is(ModItems.IRON_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.ironSpeedMultiplier() * 100) - 100;
                    } else if (stack.is(ModItems.DIAMOND_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.diamondSpeedMultiplier() * 100) - 100;
                    } else if (stack.is(ModItems.NETHERITE_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.netheriteSpeedMultiplier() * 100) - 100;
                    } else {
                        speedBonus = 0;
                    }
                    MutableComponent text = Component.literal("+%s%s ".formatted(speedBonus, "%"));
                    text.append(Component.translatable("item.happierghasts.propeller_speed.tooltip"));
                    list.add(text.withStyle(ChatFormatting.BLUE));
                    break;
                }
            }
        });
    }
}