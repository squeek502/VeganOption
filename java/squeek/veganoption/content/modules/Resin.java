package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.blocks.DamagedSpruceLogBlock;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.loot.GenericBlockLootSubProvider;

import java.util.List;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.REGISTER_BLOCKS;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class Resin implements IContentModule
{
	public static Supplier<Item> resin;
	public static Supplier<Item> rosin;
	public static DeferredHolder<Block, DamagedSpruceLogBlock> damagedSpruceLog;
	public static DeferredHolder<Item, BlockItem> damagedSpruceLogItem;

	@Override
	public void create()
	{
		resin = REGISTER_ITEMS.register("resin", () -> new Item(new Item.Properties()));
		rosin = REGISTER_ITEMS.register("rosin", () -> new Item(new Item.Properties()));
		damagedSpruceLog = REGISTER_BLOCKS.register("damaged_spruce_log", DamagedSpruceLogBlock::new);
		damagedSpruceLogItem = REGISTER_ITEMS.register("damaged_spruce_log", () -> new BlockItem(damagedSpruceLog.get(), new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.ROSIN).add(rosin.get());
		provider.tagW(ContentHelper.ItemTags.RESIN).add(resin.get());
		provider.tagW(ContentHelper.ItemTags.SLIMEBALLS).add(resin.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(resin.get());
		provider.basicItem(rosin.get());
		provider.withExistingParent(damagedSpruceLogItem.getId().toString(), provider.mcLoc("block/cube"))
			.texture("up", provider.mcLoc("block/spruce_log_top"))
			.texture("down", provider.mcLoc("block/spruce_log_top"))
			.texture("north", provider.modLoc("block/damaged_spruce_log"))
			.texture("south", provider.mcLoc("block/spruce_log"))
			.texture("east", provider.mcLoc("block/spruce_log"))
			.texture("west", provider.mcLoc("block/spruce_log"));
	}

	@Override
	public void datagenBlockTags(DataGenProviders.BlockTags provider)
	{
		provider.tagW(BlockTags.LOGS).add(damagedSpruceLog.get());
		provider.tagW(BlockTags.LOGS_THAT_BURN).add(damagedSpruceLog.get());
		provider.tagW(BlockTags.SPRUCE_LOGS).add(damagedSpruceLog.get());
		provider.tagW(BlockTags.MINEABLE_WITH_AXE).add(damagedSpruceLog.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(resin.get()), RecipeCategory.MISC, rosin.get(), 0.2f, ContentHelper.DEFAULT_SMELT_TIME)
			.unlockedBy("has_resin", provider.hasW(resin.get()))
			.save(output);
	}

	@Nullable
	@Override
	public BlockLootSubProvider getBlockLootProvider()
	{
		return new GenericBlockLootSubProvider() {
			@Override
			protected void generate()
			{
				add(damagedSpruceLog.get(),
					LootTable.lootTable()
						.withPool(
							LootPool.lootPool()
								.add(LootItem.lootTableItem(resin.get()))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(damagedSpruceLog.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DamagedSpruceLogBlock.NORTH_HARDENED, true))))
						.withPool(
							LootPool.lootPool()
								.add(LootItem.lootTableItem(resin.get()))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(damagedSpruceLog.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DamagedSpruceLogBlock.SOUTH_HARDENED, true))))
						.withPool(
							LootPool.lootPool()
								.add(LootItem.lootTableItem(resin.get()))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(damagedSpruceLog.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DamagedSpruceLogBlock.EAST_HARDENED, true))))
						.withPool(
							LootPool.lootPool()
								.add(LootItem.lootTableItem(resin.get()))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(damagedSpruceLog.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DamagedSpruceLogBlock.WEST_HARDENED, true))))
						.withPool(
							LootPool.lootPool()
								.add(LootItem.lootTableItem(Items.SPRUCE_LOG))
						)
				);
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return List.of(damagedSpruceLog.get());
			}
		};
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.SLIME_BALL), () -> Ingredient.of(ContentHelper.ItemTags.SLIMEBALLS));
	}
}
