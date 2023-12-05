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
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.blocks.BlockBedStraw;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.loot.GenericBlockLootSubProvider;
import squeek.veganoption.items.ItemBedStraw;

import java.util.List;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.REGISTER_BLOCKS;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class StrawBed implements IContentModule
{
	public static RegistryObject<Block> bedStrawBlock;
	public static RegistryObject<Item> bedStrawItem;

	private static final String TEXTURE_BASE = MODID_LOWER + ":blocks/straw_bed_";
	private static final String TEXTURE_BOTTOM = "minecraft:blocks/planks_oak";

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
		// todo: put htis back in json
		provider.getVariantBuilder(bedStrawBlock.get()).forAllStatesExcept(state -> {
			Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
			BedPart part = state.getValue(BedBlock.PART);
			ResourceLocation loc = provider.models().modLoc("blocks/straw_bed_" + part);
			String top = TEXTURE_BASE + part + "_top";
			String end = TEXTURE_BASE + part + "_end";
			String side = TEXTURE_BASE + part + "_side";
			return ConfiguredModel.builder().modelFile(provider.models().getBuilder(loc.toString())
				.element()
				.from(0, 0, 0)
				.to(16, 9, 16)
				.face(Direction.UP).texture(top).uvs(0, 16, 16, 0).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
				.face(Direction.NORTH).texture(end).uvs(0, 7, 16, 16).end()
				.face(Direction.WEST).texture(side).uvs(0, 7, 16, 16).end()
				.face(Direction.EAST).texture(side).uvs(16, 7, 0, 16).end()
				.end()
				.element()
				.from(0, 3, 0)
				.to(16, 3, 16)
				.face(Direction.DOWN).texture(TEXTURE_BOTTOM).uvs(0, 0, 16, 16).end()
				.end()
			)
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
