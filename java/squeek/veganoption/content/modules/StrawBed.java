package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Direction;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.blocks.BlockBedStraw;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.items.ItemBedStraw;
import squeek.veganoption.loot.GenericBlockLootSubProvider;

import java.util.List;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.REGISTER_BLOCKS;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class StrawBed implements IContentModule
{
	public static Supplier<Block> bedStrawBlock;
	public static Supplier<Item> bedStrawItem;

	@Override
	public void create()
	{
		bedStrawBlock = REGISTER_BLOCKS.register("straw_bed", BlockBedStraw::new);
		bedStrawItem = REGISTER_ITEMS.register("straw_bed", () -> new ItemBedStraw((BedBlock) bedStrawBlock.get()));
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bedStrawItem.get())
			.pattern("~~~")
			.pattern("===")
			.define('~', Items.HAY_BLOCK)
			.define('=', ContentHelper.ItemTags.WOOD_PLANKS)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(bedStrawItem.get());
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.getVariantBuilder(bedStrawBlock.get()).forAllStatesExcept(state -> {
			Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
			BedPart part = state.getValue(BedBlock.PART);
			ResourceLocation loc = provider.models().modLoc("block/straw_bed_" + part);

			return ConfiguredModel.builder()
				.modelFile(provider.models().getExistingFile(loc))
				.rotationY((int) facing.toYRot())
				.build();
		}, BedBlock.OCCUPIED);
	}

	@Nullable
	@Override
	public BlockLootSubProvider getBlockLootProvider()
	{
		return new GenericBlockLootSubProvider() {
			@Override
			protected void generate()
			{
				add(bedStrawBlock.get(), createSinglePropConditionTable(bedStrawBlock.get(), BedBlock.PART, BedPart.HEAD));
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return List.of(bedStrawBlock.get());
			}
		};
	}
}
