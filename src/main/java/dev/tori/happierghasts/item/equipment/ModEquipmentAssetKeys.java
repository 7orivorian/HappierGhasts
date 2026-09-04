package dev.tori.happierghasts.item.equipment;

import dev.tori.happierghasts.item.PropellerMaterial;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.EquipmentAsset;
import java.util.Map;

import static dev.tori.happierghasts.HappierGhasts.MOD_ID;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public interface ModEquipmentAssetKeys {

    ResourceKey<? extends Registry<EquipmentAsset>> REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(MOD_ID, "equipment_asset"));
    Map<PropellerMaterial, ResourceKey<EquipmentAsset>> PROPELLER_FROM_MATERIAL = Util.makeEnumMap(PropellerMaterial.class, material -> register(material.getSerializedName() + "_propeller"));

    static ResourceKey<EquipmentAsset> register(String name) {
        return ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }
}