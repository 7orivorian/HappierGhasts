package dev.tori.happierghasts.item;

import dev.tori.happierghasts.HappierGhasts;
import dev.tori.happierghasts.item.equipment.ModEquipmentAssetKeys;
import dev.tori.happierghasts.item.items.PropellerItem;
import dev.tori.happierghasts.mixin.accessor.ItemsAccessor;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.tori.happierghasts.HappierGhasts.LOGGER;
import static dev.tori.happierghasts.HappierGhasts.MOD_ID;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class ModItems {

    public static final Item COPPER_PROPELLER = register("copper_propeller", PropellerItem::new, new Item.Properties().stacksTo(1).component(DataComponents.EQUIPPABLE, ofPropeller(PropellerMaterial.COPPER)));
    public static final Item IRON_PROPELLER = register("iron_propeller", PropellerItem::new, new Item.Properties().stacksTo(1).component(DataComponents.EQUIPPABLE, ofPropeller(PropellerMaterial.IRON)));
    public static final Item DIAMOND_PROPELLER = register("diamond_propeller", PropellerItem::new, new Item.Properties().stacksTo(1).component(DataComponents.EQUIPPABLE, ofPropeller(PropellerMaterial.DIAMOND)));
    public static final Item NETHERITE_PROPELLER = register("netherite_propeller", PropellerItem::new, new Item.Properties().stacksTo(1).component(DataComponents.EQUIPPABLE, ofPropeller(PropellerMaterial.NETHERITE)));
    public static final ArrayList<Item> PROPELLERS = new ArrayList<>() {{
        add(COPPER_PROPELLER);
        add(IRON_PROPELLER);
        add(DIAMOND_PROPELLER);
        add(NETHERITE_PROPELLER);
    }};

    public static void registerItemGroups() {
        LOGGER.debug("Registering item groups...");

        OwoItemGroup.builder(
                Identifier.fromNamespaceAndPath(HappierGhasts.MOD_ID, "happier_ghasts"),
                () -> Icon.of(Items.HARNESS.purple())
        ).initializer(group -> {
            ItemGroupTab.ContentSupplier supplier = (context, entries) -> entries.acceptAll(PROPELLERS.stream().map(Item::getDefaultInstance).collect(Collectors.toList()));
            group.addCustomTab(Icon.of(Items.HARNESS.purple()), "happierghasts", supplier, ItemGroupTab.DEFAULT_TEXTURE, true);
        }).build().initialize();
    }

    public static Equippable ofPropeller(PropellerMaterial material) {
        HolderGetter<EntityType<?>> lookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);
        return Equippable.builder(EquipmentSlot.FEET)
                .setEquipSound(SoundEvents.HARNESS_EQUIP)
                .setAsset(ModEquipmentAssetKeys.PROPELLER_FROM_MATERIAL.get(material))
                .setAllowedEntities(lookup.getOrThrow(EntityTypeTags.CAN_EQUIP_HARNESS))
                .setEquipOnInteract(true)
                .setCanBeSheared(true)
                .setShearingSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.HARNESS_UNEQUIP))
                .build();
    }

    private static Item register(String id, Item.Properties settings) {
        return register(id, Item::new, settings);
    }

    private static Item register(String id, Function<Item.Properties, Item> factory, Item.Properties settings) {
        return ItemsAccessor.callRegisterItem(keyOf(id), factory, settings);
    }

    private static ResourceKey<Item> keyOf(String id) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, id));
    }
}