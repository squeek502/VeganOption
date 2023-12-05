package squeek.veganoption.content;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import javax.annotation.Nullable;

public interface IContentModule
{
	/**
	 * Instantiate and register blocks and items
	 */
	void create();

	/**
	 * Handle anything else (called from common setup)
	 */
	default void finish() {}

	/**
	 * Called on the mod event bus on the register renderers event.
	 */
	@OnlyIn(Dist.CLIENT)
	default void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {}

	// Datagen

	/**
	 * Generate recipes
	 */
	default void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider) {}

	/**
	 * Generate blockstates and models
	 */
	default void datagenBlockStatesAndModels(BlockStateProvider provider) {}

	/**
	 * Register block tags during data generation
	 */
	default void datagenBlockTags(DataGenProviders.BlockTags provider) {}

	/**
	 * Generate fluid tags
	 */
	default void datagenFluidTags(DataGenProviders.FluidTags provider) {}

	/**
	 * Generate entity type tags
	 */
	default void datagenEntityTypeTags(EntityTypeTagsProvider provider) {}

	/**
	 * Generate item models
	 */
	default void datagenItemModels(ItemModelProvider provider) {}

	/**
	 * Generate item tags
	 */
	default void datagenItemTags(DataGenProviders.ItemTags provider) {}

	/**
	 * @return null if this module does not have any loot tables.
	 */
	@Nullable
	default BlockLootSubProvider getBlockLootProvider()
	{
		return null;
	}

	/**
	 * Generate loot modifiers
	 */
	default void datagenLootModifiers(GlobalLootModifierProvider provider) {}
}
