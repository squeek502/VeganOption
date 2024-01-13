package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.blocks.SapCauldronBlock;
import squeek.veganoption.blocks.SpoutBlock;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.loot.GenericBlockLootSubProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.REGISTER_BLOCKS;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class Syrup implements IContentModule
{
	public static DeferredHolder<Block, SapCauldronBlock> sapCauldron;
	public static Supplier<Item> sapBucket;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	public static final Map<Item, CauldronInteraction> sapInteractions = CauldronInteraction.newInteractionMap();
	public static DeferredHolder<Block, LayeredCauldronBlock> syrupCauldron;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	public static final Map<Item, CauldronInteraction> syrupInteractions = CauldronInteraction.newInteractionMap();
	public static Supplier<Item> syrupBottle;
	public static DeferredHolder<Block, SpoutBlock> spoutBlock;
	public static Supplier<Item> spoutItem;

	@Override
	public void create()
	{
		sapCauldron = REGISTER_BLOCKS.register("sap_cauldron", () -> new SapCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), sapInteractions));
		sapBucket = REGISTER_ITEMS.register("sap_bucket", () -> new Item(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
		syrupCauldron = REGISTER_BLOCKS.register("syrup_cauldron", () -> new LayeredCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), (precipitation) -> false, syrupInteractions));
		syrupBottle = REGISTER_ITEMS.register("syrup_bottle", () -> new HoneyBottleItem(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).food(Foods.HONEY_BOTTLE).stacksTo(16)));
		spoutBlock = REGISTER_BLOCKS.register("spout", SpoutBlock::new);
		spoutItem = REGISTER_ITEMS.register("spout", () -> new BlockItem(spoutBlock.get(), new Item.Properties()));
	}

	@Override
	public void finish()
	{
		CauldronInteraction.EMPTY.put(sapBucket.get(), (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) 
			{
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
				level.setBlockAndUpdate(pos, sapCauldron.get().defaultBlockState());
				level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f);
				level.scheduleTick(pos, sapCauldron.get(), SapCauldronBlock.BOIL_TIME_TICKS);
			}

			return InteractionResult.sidedSuccess(level.isClientSide());
		});
		sapInteractions.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide())
			{
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(sapBucket.get())));
				level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
			}

			return InteractionResult.sidedSuccess(level.isClientSide());
		});

		syrupInteractions.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) 
			{
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(syrupBottle.get())));
				LayeredCauldronBlock.lowerFillLevel(state, level, pos);
				level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1f, 1f);
			}

			return InteractionResult.sidedSuccess(level.isClientSide());
		});
		syrupInteractions.put(syrupBottle.get(), (state, level, pos, player, hand, stack) -> {
			if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) 
			{
				if (!level.isClientSide()) 
				{
					player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
					level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
					level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1f, 1f);
				}

				return InteractionResult.sidedSuccess(level.isClientSide());
			} else {
				return InteractionResult.PASS;
			}
		});
		CauldronInteraction.EMPTY.put(syrupBottle.get(), (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide())
			{
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
				level.setBlockAndUpdate(pos, syrupCauldron.get().defaultBlockState());
				level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1f, 1f);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		});

		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.HONEY_BOTTLE), () -> Ingredient.of(ContentHelper.ItemTags.SWEETENER));
		Modifiers.recipes.excludeOutput(Items.HONEY_BLOCK);
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.SWEETENER)
			.add(Items.HONEY_BOTTLE)
			.add(syrupBottle.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(sapBucket.get());
		provider.basicItem(syrupBottle.get());
		provider.withExistingParent(spoutBlock.getId().getPath(), provider.modLoc("block/spout"));
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.simpleBlock(
			sapCauldron.get(),
			provider.models().withExistingParent(sapCauldron.getId().getPath(),"block/template_cauldron_full")
				.texture("content", provider.modLoc("block/sap_still"))
		);
		provider.getVariantBuilder(syrupCauldron.get()).forAllStates((state) -> {
			int levelValue = state.getValue(LayeredCauldronBlock.LEVEL);
			String level = levelValue == 3 ? "_full" : "_level" + levelValue;
			return ConfiguredModel.builder()
				.modelFile(provider.models().getBuilder("block/" + syrupCauldron.getId().getPath() + level)
					.parent(provider.models().getExistingFile(provider.mcLoc("block/template_cauldron" + level)))
					.texture("content", provider.modLoc("block/syrup_still")))
				.build();
		});

		provider.getVariantBuilder(spoutBlock.get()).forAllStates((state) -> {
			StringBuilder modelPath = new StringBuilder();
			modelPath.append("block/spout");
			if (state.getValue(SpoutBlock.HAS_BUCKET))
			{
				modelPath.append("_with_bucket_");
				int level = state.getValue(SpoutBlock.LEVEL);
				if (level == 0)
					modelPath.append("empty");
				else if (level == 4)
					modelPath.append("full");
				else
					modelPath.append("level").append(level);
			}
			return ConfiguredModel.builder()
				.modelFile(provider.models().getExistingFile(provider.modLoc(modelPath.toString())))
				.rotationY(((int) state.getValue(SpoutBlock.FACING).toYRot() + 90) % 360)
				.build();
		});
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, spoutItem.get())
			.pattern(" i ")
			.pattern(" rr")
			.pattern("iii")
			.define('i', Tags.Items.INGOTS_IRON)
			.define('r', ContentHelper.ItemTags.PLASTIC_ROD)
			.unlockedBy("has_cauldron", provider.hasW(Items.CAULDRON))
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
				dropOther(sapCauldron.get(), Blocks.CAULDRON);
				dropOther(syrupCauldron.get(), Blocks.CAULDRON);
				LootItemCondition.Builder hasBucketCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(spoutBlock.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SpoutBlock.HAS_BUCKET, true));
				LootItemCondition.Builder fullCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(spoutBlock.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SpoutBlock.LEVEL, SpoutBlock.MAX_LEVEL));
				add(spoutBlock.get(),
					LootTable.lootTable()
						.withPool(LootPool.lootPool()
							.add(LootItem.lootTableItem(Items.BUCKET))
							.when(hasBucketCondition)
							.when(InvertedLootItemCondition.invert(fullCondition)))
						.withPool(LootPool.lootPool()
							.add(LootItem.lootTableItem(sapBucket.get()))
							.when(hasBucketCondition)
							.when(fullCondition))
						.withPool(LootPool.lootPool()
							.add(LootItem.lootTableItem(spoutItem.get())))
				);
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return List.of(spoutBlock.get(), sapCauldron.get(), syrupCauldron.get());
			}
		};
	}

	@Override
	public void datagenBlockTags(DataGenProviders.BlockTags provider)
	{
		provider.tagW(BlockTags.CAULDRONS)
			.add(sapCauldron.get())
			.add(syrupCauldron.get());
	}
}
