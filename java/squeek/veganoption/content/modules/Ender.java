package squeek.veganoption.content.modules;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import squeek.veganoption.blocks.BlockEncrustedObsidian;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.blocks.BlockRawEnder;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.fluids.GenericFluidTypeRenderProperties;
import squeek.veganoption.fluids.RawEnderFluid;
import squeek.veganoption.items.GenericBucketItem;
import squeek.veganoption.loot.GenericBlockLootSubProvider;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.*;

public class Ender implements IContentModule
{
	public static DeferredHolder<Block, BlockEncrustedObsidian> encrustedObsidian;
	public static Supplier<Item> encrustedObsidianItem;
	public static Supplier<Block> enderRift;
	public static Supplier<BlockEntityType<TileEntityEnderRift>> enderRiftType;
	public static Supplier<FluidType> rawEnderFluidType;
	public static Supplier<Fluid> rawEnderStill;
	public static Supplier<Fluid> rawEnderFlowing;
	public static Supplier<Block> rawEnderBlock;
	public static Supplier<Item> rawEnderBucket;
	public static final int RAW_ENDER_PER_PEARL = FluidType.BUCKET_VOLUME;

	@Override
	public void create()
	{
		encrustedObsidian = REGISTER_BLOCKS.register("encrusted_obsidian", BlockEncrustedObsidian::new);
		encrustedObsidianItem = REGISTER_ITEMS.register("encrusted_obsidian", () -> new BlockItem(encrustedObsidian.get(), new Item.Properties()));

		enderRift = REGISTER_BLOCKS.register("ender_rift", BlockEnderRift::new);
		enderRiftType = REGISTER_BLOCKENTITIES.register("ender_rift", () -> BlockEntityType.Builder.of(TileEntityEnderRift::new, enderRift.get()).build(null));

		BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(() -> rawEnderFluidType.get(), () -> rawEnderStill.get(), () -> rawEnderFlowing.get())
			.block(() -> (LiquidBlock) rawEnderBlock.get())
			.bucket(() -> rawEnderBucket.get());

		rawEnderFluidType = REGISTER_FLUIDTYPES.register("raw_ender", () ->
			new FluidType(FluidType.Properties
				.create()
				.lightLevel(3)
				.viscosity(3000)
				.density(4000)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
					@Override
					public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
					{
						consumer.accept(new GenericFluidTypeRenderProperties("raw_ender", 0x12634F));
					}
			});

		rawEnderStill = REGISTER_FLUIDS.register("raw_ender", () -> new RawEnderFluid.Still(fluidProperties));
		rawEnderFlowing = REGISTER_FLUIDS.register("raw_ender_flowing", () -> new RawEnderFluid.Flowing(fluidProperties));
		rawEnderBlock = REGISTER_BLOCKS.register("raw_ender", BlockRawEnder::new);
		rawEnderBucket = REGISTER_ITEMS.register("raw_ender_bucket", () -> new GenericBucketItem(() -> rawEnderStill.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(rawEnderBucket.get(), encrustedObsidianItem.get());
	}

	@Override
	public void datagenBlockTags(DataGenProviders.BlockTags provider)
	{
		provider.tagW(BlockTags.MINEABLE_WITH_PICKAXE).add(encrustedObsidian.get());
		provider.tagW(BlockTags.NEEDS_DIAMOND_TOOL).add(encrustedObsidian.get());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(enderRiftType.get(), TheEndPortalRenderer::new);
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.simpleBlock(encrustedObsidian.get());
		provider.simpleBlock(enderRift.get(), provider.models().getExistingFile(provider.mcLoc("block/end_portal")));
		provider.getVariantBuilder(rawEnderBlock.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(provider.models().getExistingFile(provider.modLoc("raw_ender"))).build());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.withExistingParent(encrustedObsidian.getId().getPath(), provider.modLoc("block/encrusted_obsidian"));
		provider.basicItem(rawEnderBucket.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, encrustedObsidianItem.get(), 2)
			.requires(Items.DIAMOND)
			.requires(Items.OBSIDIAN).requires(Items.OBSIDIAN)
			.requires(Items.EMERALD)
			.unlockedBy("has_obsidian", provider.hasW(Items.OBSIDIAN))
			.save(output);
	}

	@Override
	public BlockLootSubProvider getBlockLootProvider()
	{
		return new GenericBlockLootSubProvider() {
			@Override
			protected void generate()
			{
				dropSelf(encrustedObsidian.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return List.of(encrustedObsidian.get());
			}
		};
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void finishClient(FMLClientSetupEvent event)
	{
		ItemBlockRenderTypes.setRenderLayer(rawEnderStill.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(rawEnderFlowing.get(), RenderType.translucent());
	}
}
