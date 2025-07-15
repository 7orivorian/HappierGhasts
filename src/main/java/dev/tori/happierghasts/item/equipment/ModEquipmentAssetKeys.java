package dev.tori.happierghasts.item.equipment;

import dev.tori.happierghasts.item.PropellerMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

import static dev.tori.happierghasts.HappierGhasts.MOD_ID;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public interface ModEquipmentAssetKeys {

    RegistryKey<? extends Registry<EquipmentAsset>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(MOD_ID, "equipment_asset"));
    Map<PropellerMaterial, RegistryKey<EquipmentAsset>> PROPELLER_FROM_MATERIAL = Util.mapEnum(PropellerMaterial.class, material -> register(material.asString() + "_propeller"));

    static RegistryKey<EquipmentAsset> register(String name) {
        return RegistryKey.of(REGISTRY_KEY, Identifier.of(MOD_ID, name));
    }
}