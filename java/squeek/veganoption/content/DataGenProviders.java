package squeek.veganoption.content;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentModuleHandler;
import squeek.veganoption.loot.ReplaceLootModifier;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static squeek.veganoption.VeganOption.REGISTER_LOOTMODIFIERS;

public class DataGenProviders
{
	public static void generateData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		boolean client = event.includeClient();
		boolean server = event.includeServer();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();

		generator.addProvider(client, new BlockStates(packOutput, ModInfo.MODID_LOWER, fileHelper));
		generator.addProvider(client, new ItemModels(packOutput, ModInfo.MODID_LOWER, fileHelper));
		BlockTags blockTagsProvider = new BlockTags(packOutput, lookupProvider, ModInfo.MODID_LOWER, fileHelper);
		generator.addProvider(server, blockTagsProvider);
		generator.addProvider(server, new ItemTags(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), ModInfo.MODID_LOWER, fileHelper));
		generator.addProvider(server, new EntityTypeTags(packOutput, lookupProvider));
		generator.addProvider(server, new Recipes(packOutput, lookupProvider));

		List<LootTableProvider.SubProviderEntry> subProviders = Lists.newArrayList();
		ContentModuleHandler.iterateOverModules(module -> {
			BlockLootSubProvider blockProvider = module.getBlockLootProvider();
			if (blockProvider != null)
			{
				subProviders.add(new LootTableProvider.SubProviderEntry(() -> blockProvider, LootContextParamSets.BLOCK));
			}
		});
		generator.addProvider(server, new LootTables(packOutput, Collections.emptySet(), subProviders));
		generator.addProvider(server, new LootModifiers(packOutput));
	}

	public static class ItemTags extends ItemTagsProvider
	{
		public ItemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> tagLookup, String modid, ExistingFileHelper existingFileHelper)
		{
			super(packOutput, lookupProvider, tagLookup, modid, existingFileHelper);
		}

		/**
		 * Public wrapper for {@link ItemTagsProvider#tag(TagKey)} so subclasses can actually work.
		 * TODO: access transformer
		 */
		public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tagW(TagKey<Item> key) {
			return tag(key);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider)
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenItemTags(this));
		}
	}

	public static class BlockStates extends BlockStateProvider
	{
		public BlockStates(PackOutput output, String modid, ExistingFileHelper exFileHelper)
		{
			super(output, modid, exFileHelper);
		}

		@Override
		protected void registerStatesAndModels()
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenBlockStatesAndModels(this));
		}
	}

	public static class BlockTags extends BlockTagsProvider
	{
		public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper)
		{
			super(output, lookupProvider, modId, existingFileHelper);
		}

		/**
		 * Public wrapper for {@link BlockTagsProvider#tag(TagKey)} so subclasses can actually work.
		 * TODO: access transformer
		 */
		public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tagW(TagKey<Block> key) {
			return tag(key);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider)
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenBlockTags(this));
		}
	}

	public static class FluidTags extends FluidTagsProvider
	{
		public FluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper)
		{
			super(output, lookupProvider, modId, existingFileHelper);
		}

		/**
		 * Public wrapper for {@link FluidTagsProvider#tag(TagKey)} so subclasses can actually work.
		 * TODO: access transformer
		 */
		public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Fluid> tagW(TagKey<Fluid> key) {
			return tag(key);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider)
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenFluidTags(this));
		}
	}

	public static class EntityTypeTags extends EntityTypeTagsProvider
	{
		public EntityTypeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
		{
			super(packOutput, lookupProvider);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider)
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenEntityTypeTags(this));
		}
	}

	public static class ItemModels extends ItemModelProvider
	{
		public ItemModels(PackOutput output, String modid, ExistingFileHelper existingFileHelper)
		{
			super(output, modid, existingFileHelper);
		}

		@Override
		protected void registerModels()
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenItemModels(this));
		}
	}

	public static class Recipes extends RecipeProvider
	{
		public Recipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
		{
			super(packOutput, lookupProvider);
		}

		/**
		 * Public wrapper for {@link RecipeProvider#has(ItemLike)}.
		 */
		public Criterion<InventoryChangeTrigger.TriggerInstance> hasW(ItemLike itemLike)
		{
			return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
		}

		@Override
		protected void buildRecipes(RecipeOutput output)
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenRecipes(output, this));
		}
	}

	public static class LootTables extends LootTableProvider
	{
		public LootTables(PackOutput packOutput, Set<ResourceLocation> registryNames, List<SubProviderEntry> subProviders)
		{
			super(packOutput, registryNames, subProviders);
		}

		@Override
		public List<SubProviderEntry> getTables()
		{
			return super.getTables();
		}
	}

	public static class LootModifiers extends GlobalLootModifierProvider
	{
		public LootModifiers(PackOutput output)
		{
			super(output, ModInfo.MODID_LOWER);
		}

		@Override
		protected void start()
		{
			ContentModuleHandler.iterateOverModules(module -> module.datagenLootModifiers(this));
		}
	}
}
