package dev.tori.happierghasts.client;

import dev.tori.happierghasts.HappierGhasts;
import dev.tori.happierghasts.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class HappierGhastsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, list) -> {
            for (Item propeller : ModItems.PROPELLERS) {
                if (stack.isOf(propeller)) {
                    list.add(Text.empty());
                    list.add(Text.translatable("item.happierghasts.propeller_effect.tooltip").formatted(Formatting.GRAY));

                    int speedBonus;
                    if (stack.isOf(ModItems.COPPER_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.copperPropellerSpeedMultiplier() * 100) - 100;
                    } else if (stack.isOf(ModItems.IRON_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.ironPropellerSpeedMultiplier() * 100) - 100;
                    } else if (stack.isOf(ModItems.DIAMOND_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.diamondPropellerSpeedMultiplier() * 100) - 100;
                    } else if (stack.isOf(ModItems.NETHERITE_PROPELLER)) {
                        speedBonus = (int) (HappierGhasts.CONFIG.propellers.netheritePropellerSpeedMultiplier() * 100) - 100;
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