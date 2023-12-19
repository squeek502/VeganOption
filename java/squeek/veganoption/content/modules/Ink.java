package squeek.veganoption.content.modules;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
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
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.fluids.GenericFluidTypeRenderProperties;
import squeek.veganoption.helpers.FluidHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.*;

public class Ink implements IContentModule
{
	public static Supplier<Item> blackVegetableOilInk;
	public static Supplier<Item> blackVegetableOilInkBucket;
	public static Supplier<Item> waxVegetable;
	public static Supplier<FluidType> blackInkFluidType;
	public static Supplier<Fluid> blackInkFluidStill;
	public static Supplier<Fluid> blackInkFluidFlowing;
	public static Supplier<Block> blackInk;

	@Override
	public void create()
	{
		waxVegetable = REGISTER_ITEMS.register("vegetable_wax", () -> new Item(new Item.Properties()));

		BaseFlowingFluid.Properties blackInkProperties = new BaseFlowingFluid.Properties(() -> blackInkFluidType.get(), () -> blackInkFluidStill.get(), () -> blackInkFluidFlowing.get())
			.block(() -> (LiquidBlock) blackInk.get())
			.bucket(() -> blackVegetableOilInkBucket.get());
		blackVegetableOilInk = REGISTER_ITEMS.register("vegetable_oil_ink_black", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE)));
		blackInkFluidType = REGISTER_FLUIDTYPES.register("black_ink", () ->
			new FluidType(FluidType.Properties.create()
				.density(900)
				.viscosity(1100)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
				@Override
				public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
				{
					consumer.accept(new GenericFluidTypeRenderProperties("black_ink", 0xEFC600));
				}
		});
		blackInkFluidStill = REGISTER_FLUIDS.register("black_ink", () -> new BaseFlowingFluid.Source(blackInkProperties));
		blackInkFluidFlowing = REGISTER_FLUIDS.register("black_ink_flowing", () -> new BaseFlowingFluid.Flowing(blackInkProperties));
		blackInk = REGISTER_BLOCKS.register("black_ink", () -> new LiquidBlock(() -> (FlowingFluid) blackInkFluidStill.get(), BlockBehaviour.Properties.of().noLootTable()));
		blackVegetableOilInkBucket = REGISTER_ITEMS.register("vegetable_oil_ink_black_bucket", () -> new BucketItem(() -> blackInkFluidStill.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.DYES_BLACK).add(blackVegetableOilInk.get());

		provider.tagW(ContentHelper.ItemTags.PIGMENT_BLACK).add(Items.CHARCOAL);
		provider.tagW(ContentHelper.ItemTags.PIGMENT_WHITE).add(Items.QUARTZ);

		provider.tagW(ContentHelper.ItemTags.INK_BLACK)
			.add(Items.INK_SAC)
			.add(blackVegetableOilInk.get());

		provider.tagW(ContentHelper.ItemTags.WAX).add(waxVegetable.get());
	}

	@Override
	public void datagenFluidTags(DataGenProviders.FluidTags provider)
	{
		provider.tagW(ContentHelper.FluidTags.BLACK_INK)
			.add(blackInkFluidStill.get())
			.add(blackInkFluidFlowing.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(ContentHelper.ItemTags.VEGETABLE_OIL), RecipeCategory.MISC, waxVegetable.get(), 0.2f, ContentHelper.DEFAULT_SMELT_TIME);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, blackVegetableOilInk.get())
			.requires(ContentHelper.ItemTags.VEGETABLE_OIL)
			.requires(ContentHelper.ItemTags.WAX)
			.requires(ContentHelper.ItemTags.ROSIN)
			.requires(ContentHelper.ItemTags.PIGMENT_BLACK)
			.unlockedBy("has_vegetable_oil", provider.hasW(VegetableOil.vegetableOilBottle.get()))
			.save(output);
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.INK_SAC), () -> Ingredient.of(ContentHelper.ItemTags.INK_BLACK));

		Modifiers.crafting.addInputsToRemoveForOutput(blackVegetableOilInk.get(), () -> new Ingredient[] { Ingredient.of(ContentHelper.ItemTags.VEGETABLE_OIL) });

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(blackInkFluidStill.get(), FluidType.BUCKET_VOLUME), new FluidStack(VegetableOil.fluidVegetableOilStill.get(), FluidType.BUCKET_VOLUME), new InputItemStack(ContentHelper.ItemTags.WAX, FluidHelper.BOTTLES_PER_BUCKET), new InputItemStack(ContentHelper.ItemTags.ROSIN, FluidHelper.BOTTLES_PER_BUCKET), new InputItemStack(ContentHelper.ItemTags.PIGMENT_BLACK, FluidHelper.BOTTLES_PER_BUCKET)));

		Modifiers.bottles.registerCustomBottleHandler(ContentHelper.FluidTags.BLACK_INK, () -> new ItemStack(blackVegetableOilInk.get()), (stack) -> stack.getItem() == blackVegetableOilInk.get(), blackInkFluidStill);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void finishClient(FMLClientSetupEvent event)
	{
		ItemBlockRenderTypes.setRenderLayer(blackInkFluidStill.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(blackInkFluidFlowing.get(), RenderType.translucent());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(blackVegetableOilInk.get());
		provider.basicItem(waxVegetable.get());
		provider.basicItem(blackVegetableOilInkBucket.get());
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.getVariantBuilder(blackInk.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(provider.models().getExistingFile(provider.modLoc("black_ink"))).build());
	}
}
