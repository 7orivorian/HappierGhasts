package dev.tori.happierghasts.item;

import dev.tori.happierghasts.HappierGhasts;
import dev.tori.happierghasts.item.equipment.ModEquipmentAssetKeys;
import dev.tori.happierghasts.item.items.PropellerItem;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

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

    public static final Item COPPER_PROPELLER = register("copper_propeller", PropellerItem::new, new Item.Settings().maxCount(1).component(DataComponentTypes.EQUIPPABLE, ofPropeller(PropellerMaterial.COPPER)));
    public static final Item IRON_PROPELLER = register("iron_propeller", PropellerItem::new, new Item.Settings().maxCount(1).component(DataComponentTypes.EQUIPPABLE, ofPropeller(PropellerMaterial.IRON)));
    public static final Item DIAMOND_PROPELLER = register("diamond_propeller", PropellerItem::new, new Item.Settings().maxCount(1).component(DataComponentTypes.EQUIPPABLE, ofPropeller(PropellerMaterial.DIAMOND)));
    public static final Item NETHERITE_PROPELLER = register("netherite_propeller", PropellerItem::new, new Item.Settings().maxCount(1).component(DataComponentTypes.EQUIPPABLE, ofPropeller(PropellerMaterial.NETHERITE)));
    public static final ArrayList<Item> PROPELLERS = new ArrayList<>() {{
        add(COPPER_PROPELLER);
        add(IRON_PROPELLER);
        add(DIAMOND_PROPELLER);
        add(NETHERITE_PROPELLER);
    }};

    public static void registerItemGroups() {
        LOGGER.debug("Registering item groups...");

        OwoItemGroup.builder(
                Identifier.of(HappierGhasts.MOD_ID, "happier_ghasts"),
                () -> Icon.of(Items.PURPLE_HARNESS)
        ).initializer(group -> {
            ItemGroupTab.ContentSupplier supplier = (context, entries) -> entries.addAll(PROPELLERS.stream().map(Item::getDefaultStack).collect(Collectors.toList()));
            group.addCustomTab(Icon.of(Items.PURPLE_HARNESS), "happierghasts", supplier, ItemGroupTab.DEFAULT_TEXTURE, true);
        }).build().initialize();
    }

    public static EquippableComponent ofPropeller(PropellerMaterial material) {
        RegistryEntryLookup<EntityType<?>> registryEntryLookup = Registries.createEntryLookup(Registries.ENTITY_TYPE);
        return EquippableComponent.builder(EquipmentSlot.FEET)
                .equipSound(SoundEvents.ENTITY_HAPPY_GHAST_EQUIP)
                .model(ModEquipmentAssetKeys.PROPELLER_FROM_MATERIAL.get(material))
                .allowedEntities(registryEntryLookup.getOrThrow(EntityTypeTags.CAN_EQUIP_HARNESS))
                .equipOnInteract(true)
                .canBeSheared(true)
                .shearingSound(Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_HAPPY_GHAST_UNEQUIP))
                .build();
    }

    private static Item register(String id, Item.Settings settings) {
        return register(id, Item::new, settings);
    }

    private static Item register(String id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        return Items.register(keyOf(id), factory, settings);
    }

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, id));
    }
}