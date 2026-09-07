package dev.tori.happierghasts.item;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider registries) {
        builder(ItemTags.HAPPY_GHAST_TEMPT_ITEMS)
                .add(ModItemIds.COPPER_PROPELLER)
                .add(ModItemIds.IRON_PROPELLER)
                .add(ModItemIds.DIAMOND_PROPELLER)
                .add(ModItemIds.NETHERITE_PROPELLER);
    }
}