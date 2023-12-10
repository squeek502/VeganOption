package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.blocks.BlockJutePlant;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.loot.GenericBlockLootSubProvider;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

import java.util.List;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.REGISTER_BLOCKS;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID_LOWER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Jute implements IContentModule
{
	public static RegistryObject<Block> juteBundled;
	public static RegistryObject<Item> juteBundledItem;
	public static RegistryObject<Block> jutePlant;
	public static RegistryObject<Item> juteSeeds;
	public static RegistryObject<Item> juteStalk;
	public static RegistryObject<Item> juteFibre;

	public static final int JUTE_BASE_COLOR = 0x67ce0c;
	public static final int JUTE_RETTED_COLOR = 0xbfb57e;

	private static final String TINTED_CUBE_COLUMN = "block/tinted_cube_column";
	private static final String TINTED_CROSS = "block/tinted_cross";

	@Override
	public void create()
	{
		juteFibre = REGISTER_ITEMS.register("jute_fibre", () -> new Item(new Item.Properties()));
		juteStalk = REGISTER_ITEMS.register("jute_stalk", () -> new Item(new Item.Properties()));
		juteBundled = REGISTER_BLOCKS.register("bundled_jute", () -> new BlockRettable(juteFibre, 8, 15));
		juteBundledItem = REGISTER_ITEMS.register("bundled_jute", () -> new BlockItem(juteBundled.get(), new Item.Properties()));
		jutePlant = REGISTER_BLOCKS.register("jute_plant", BlockJutePlant::new);
		juteSeeds = REGISTER_ITEMS.register("jute_seeds", () -> new ItemNameBlockItem(jutePlant.get(), new Item.Properties()));
	}

	@Override
	public void datagenBlockTags(DataGenProviders.BlockTags provider)
	{
		provider.tagW(BlockTags.MINEABLE_WITH_AXE).add(juteBundled.get());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event)
	{
		event.register(new BlockRettable.ColorHandler(JUTE_BASE_COLOR, JUTE_RETTED_COLOR), juteBundledItem.get());
		event.register(new BlockJutePlant.ColorHandler(), juteSeeds.get());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event)
	{
		event.register(new BlockRettable.ColorHandler(JUTE_BASE_COLOR, JUTE_RETTED_COLOR), juteBundled.get());
		event.register(new BlockJutePlant.ColorHandler(), jutePlant.get());
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		ModelFile bundledJuteModelFile = provider.models().getBuilder("block/" + juteBundled.getId().getPath())
			.parent(provider.models().getExistingFile(provider.modLoc(TINTED_CUBE_COLUMN)))
			.texture("side", provider.modLoc("block/" + juteBundled.getId().getPath() + "_side"))
			.texture("end", provider.modLoc("block/" + juteBundled.getId().getPath() + "_end"));

		provider.getVariantBuilder(juteBundled.get())
			.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
			.modelForState().modelFile(bundledJuteModelFile).rotationX(90).rotationY(90).addModel()
			.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
			.modelForState().modelFile(bundledJuteModelFile).addModel()
			.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
			.modelForState().modelFile(bundledJuteModelFile).rotationX(90).addModel();

		// growth stages 0-5 are for the bottom, and 6-10 are for the top. 11 is a special case for the bottom half when it has a top
		provider.getVariantBuilder(jutePlant.get()).forAllStates(state -> {
			int stage = state.getValue(BlockJutePlant.GROWTH_STAGE);
			int textureIndex = stage;
			if (stage == 11)
				textureIndex = 6;
			else if (stage > 5)
				textureIndex -= 6;
			return ConfiguredModel.builder()
				.modelFile(provider.models().getBuilder("block/" + jutePlant.getId().getPath() + "_" + stage)
					.parent(provider.models().getExistingFile(provider.mcLoc(TINTED_CROSS)))
					.texture("cross", provider.modLoc("block/" + jutePlant.getId().getPath() + "_" + textureIndex))
					.renderType("cutout_mipped"))
				.build();
		});
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(juteStalk.get());
		provider.basicItem(juteFibre.get());
		provider.basicItem(juteSeeds.get());

		provider.withExistingParent(juteBundledItem.getId().toString(), provider.modLoc(TINTED_CUBE_COLUMN))
			.texture("side", provider.modLoc("block/" + juteBundled.getId().getPath() + "_side"))
			.texture("end", provider.modLoc("block/" + juteBundled.getId().getPath() + "_end"));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.FIBRES).add(juteFibre.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, juteBundledItem.get())
			.pattern("///")
			.pattern("///")
			.pattern("///")
			.define('/', juteStalk.get())
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, juteSeeds.get())
			.requires(juteStalk.get())
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.STRING)
			.pattern("~~")
			.define('~', ContentHelper.ItemTags.FIBRES)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"large_fern_jute_stalk",
			new SimpleBlockDropLootModifier(
				Blocks.LARGE_FERN,
				juteStalk.get(),
				ConstantValue.exactly(1),
				UniformGenerator.between(1, 3)
			)
		);
	}

	@Override
	public BlockLootSubProvider getBlockLootProvider()
	{
		return new GenericBlockLootSubProvider()
		{
			@Override
			protected void generate()
			{
				// jute plants (partially grown ferns) bottom block drop jute seeds, but fully grown ferns only drop jute stalks
				LootPool.Builder jutePlantPool = LootPool.lootPool()
					.name("jute_plant")
					.setRolls(ConstantValue.exactly(1));
				for (int stage = 0; stage <= BlockJutePlant.NUM_GROWTH_STAGES; stage++)
				{
					if (!BlockJutePlant.isTop(stage))
					{
						jutePlantPool
							.add(LootItem.lootTableItem(juteSeeds.get())
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Jute.jutePlant.get())
									.setProperties(StatePropertiesPredicate.Builder.properties()
										.hasProperty(BlockJutePlant.GROWTH_STAGE, stage))));
					}
				}
				add(jutePlant.get(), LootTable.lootTable().withPool(jutePlantPool));

				LootItemCondition.Builder rettedCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(juteBundled.get())
					.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockRettable.STAGE, BlockRettable.MAX_RETTING_STAGES));

				LootPool.Builder rettedJutePool = LootPool.lootPool()
					.name("bundled_jute")
					.add(LootItem.lootTableItem(((BlockRettable) juteBundled.get()).getRettedItem())
						 .apply(LimitCount.limitCount(IntRange.range(((BlockRettable) juteBundled.get()).getMinRettedItemDrops(), ((BlockRettable) juteBundled.get()).getMaxRettedItemDrops())))
						 .when(rettedCondition)
						 .otherwise(LootItem.lootTableItem(juteBundledItem.get())));
				add(juteBundled.get(), LootTable.lootTable().withPool(rettedJutePool));
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return List.of(jutePlant.get(), juteBundled.get());
			}
		};
	}

	@Override
	public void finish()
	{
		CompostRegistry.addGreen(Jute.juteStalk.get());
		RelationshipRegistry.addRelationship(juteFibre.get(), juteBundledItem.get());
	}
}
