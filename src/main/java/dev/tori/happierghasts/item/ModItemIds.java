package dev.tori.happierghasts.item;

import dev.tori.happierghasts.HappierGhasts;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0.
 */
public class ModItemIds {

    public static final ResourceKey<Item> COPPER_PROPELLER = create("copper_propeller");
    public static final ResourceKey<Item> IRON_PROPELLER = create("iron_propeller");
    public static final ResourceKey<Item> DIAMOND_PROPELLER = create("diamond_propeller");
    public static final ResourceKey<Item> NETHERITE_PROPELLER = create("netherite_propeller");

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(HappierGhasts.MOD_ID, name));
    }
}