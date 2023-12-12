package squeek.veganoption.content.modules;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.blocks.BlockLyeWater;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.fluids.GenericFluidTypeRenderProperties;
import squeek.veganoption.items.ItemSoap;

import java.util.function.Consumer;

import static squeek.veganoption.VeganOption.*;

public class Soap implements IContentModule
{
	public static RegistryObject<FluidType> fluidTypeLyeWater;
	public static RegistryObject<Fluid> fluidLyeWaterStill;
	public static RegistryObject<Fluid> fluidLyeWaterFlowing;
	public static RegistryObject<Block> fluidBlockLyeWater;
	public static RegistryObject<Item> bucketLyeWater;
	public static RegistryObject<Item> soap;

	@Override
	public void create()
	{
		BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(() -> fluidTypeLyeWater.get(), () -> fluidLyeWaterStill.get(), () -> fluidLyeWaterFlowing.get())
			.block(() -> (LiquidBlock) fluidBlockLyeWater.get())
			.bucket(() -> bucketLyeWater.get());
		bucketLyeWater = REGISTER_ITEMS.register("lye_water_bucket", () -> new BucketItem(() -> fluidLyeWaterStill.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
		fluidTypeLyeWater = REGISTER_FLUIDTYPES.register("lye_water", () -> new FluidType(FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
			{
				consumer.accept(new GenericFluidTypeRenderProperties("lye_water", 0x575047));
			}
		});
		fluidLyeWaterStill = REGISTER_FLUIDS.register("lye_water", () -> new BaseFlowingFluid.Source(fluidProperties));
		fluidLyeWaterFlowing = REGISTER_FLUIDS.register("lye_water_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties));
		fluidBlockLyeWater = REGISTER_BLOCKS.register("lye_water", BlockLyeWater::new);

		soap = REGISTER_ITEMS.register("soap", ItemSoap::new);
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.SOAP).add(soap.get());
		provider.tagW(ContentHelper.ItemTags.WOOD_ASH).add(Items.CHARCOAL);
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, soap.get())
			.requires(bucketLyeWater.get())
			.requires(ContentHelper.ItemTags.VEGETABLE_OIL)
			.requires(ContentHelper.ItemTags.ROSIN)
			.unlockedBy("has_lye_water", provider.hasW(bucketLyeWater.get()))
			.save(output);
	}

	@Override
	public void finish()
	{
		Modifiers.crafting.addInputsToRemoveForOutput(bucketLyeWater.get(), Items.WATER_BUCKET);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(fluidLyeWaterStill.get(), FluidType.BUCKET_VOLUME), new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), new InputItemStack(ContentHelper.ItemTags.WOOD_ASH, 3)));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void finishClient(FMLClientSetupEvent event)
	{
		ItemBlockRenderTypes.setRenderLayer(fluidLyeWaterStill.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(fluidLyeWaterFlowing.get(), RenderType.translucent());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(soap.get());
		provider.basicItem(bucketLyeWater.get());
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.getVariantBuilder(fluidBlockLyeWater.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(provider.models().getExistingFile(provider.modLoc("lye_water"))).build());
	}
}
