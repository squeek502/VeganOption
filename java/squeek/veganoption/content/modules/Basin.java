package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.blocks.renderers.RenderBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.loot.GenericBlockLootSubProvider;

import java.util.List;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.*;

public class Basin implements IContentModule
{
	public static DeferredHolder<Block, BlockBasin> basin;
	public static Supplier<Item> basinItem;
	public static Supplier<BlockEntityType<TileEntityBasin>> basinType;

	@Override
	public void create()
	{
		basin = REGISTER_BLOCKS.register("basin", () -> new BlockBasin(
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.METAL)
				.sound(SoundType.METAL)
				.strength(2F)
				.noOcclusion()
				.isViewBlocking((state, getter, pos) -> false)));
		basinItem = REGISTER_ITEMS.register("basin", () -> new BlockItem(basin.get(), new Item.Properties()));
		basinType = REGISTER_BLOCKENTITIES.register("basin", () -> BlockEntityType.Builder.of(TileEntityBasin::new, basin.get()).build(null));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(basinType.get(), blockEntity -> new RenderBasin());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, basinItem.get())
			.pattern(" g ")
			.pattern("gcg")
			.pattern(" g ")
			.define('g', Tags.Items.GLASS_COLORLESS)
			.define('c', Items.CAULDRON)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output);
	}

	@Override
	public void datagenBlockTags(DataGenProviders.BlockTags provider)
	{
		provider.tagW(BlockTags.MINEABLE_WITH_PICKAXE).add(basin.get());
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.getVariantBuilder(basin.get())
			.partialState().with(BlockBasin.IS_OPEN, true)
			.modelForState().modelFile(provider.models().getExistingFile(provider.modLoc("block/basin_open"))).addModel()
			.partialState().with(BlockBasin.IS_OPEN, false)
			.modelForState().modelFile(provider.models().getExistingFile(provider.modLoc("block/basin_closed"))).addModel();
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.withExistingParent(basin.getId().getPath(), provider.modLoc("block/basin_closed"));
	}

	@Override
	public BlockLootSubProvider getBlockLootProvider()
	{
		return new GenericBlockLootSubProvider()
		{
			@Override
			protected void generate()
			{
				dropSelf(basin.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return List.of(basin.get());
			}
		};
	}
}
