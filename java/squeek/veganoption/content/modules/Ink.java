package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;

import static squeek.veganoption.VeganOption.*;

// todo: white ink can be removed as vanilla has added the Lily of the Valley which is used to make White Dye
public class Ink implements IContentModule
{
	public static RegistryObject<Item> blackVegetableOilInk;
	public static RegistryObject<Item> whiteVegetableOilInk;
	public static RegistryObject<Item> waxVegetable;
	public static RegistryObject<FluidType> blackInkFluidType;
	public static RegistryObject<FluidType> whiteInkFluidType;
	public static RegistryObject<Fluid> blackInkFluidStill;
	public static RegistryObject<Fluid> whiteInkFluidStill;
	public static RegistryObject<Fluid> blackInkFluidFlowing;
	public static RegistryObject<Fluid> whiteInkFluidFlowing;
	public static RegistryObject<Block> blackInk;
	public static RegistryObject<Block> whiteInk;
	public static RegistryObject<Item> blackInkBlockItem;
	public static RegistryObject<Item> whiteInkBlockItem;

	@Override
	public void create()
	{
		waxVegetable = REGISTER_ITEMS.register("vegetable_wax", () -> new Item(new Item.Properties()));

		BaseFlowingFluid.Properties blackInkProperties = new BaseFlowingFluid.Properties(() -> blackInkFluidType.get(), () -> blackInkFluidStill.get(), () -> blackInkFluidFlowing.get())
			.block(() -> (LiquidBlock) blackInk.get())
			.bucket(() -> blackVegetableOilInk.get());
		blackVegetableOilInk = REGISTER_ITEMS.register("vegetable_oil_ink_black", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE)));
		blackInkFluidType = REGISTER_FLUIDTYPES.register("black_ink", () -> new FluidType(FluidType.Properties.create()));
		blackInkFluidStill = REGISTER_FLUIDS.register("black_ink", () -> new BaseFlowingFluid.Source(blackInkProperties));
		blackInkFluidFlowing = REGISTER_FLUIDS.register("black_ink_flowing", () -> new BaseFlowingFluid.Flowing(blackInkProperties));
		blackInk = REGISTER_BLOCKS.register("black_ink", () -> new LiquidBlock(() -> (FlowingFluid) blackInkFluidStill.get(), BlockBehaviour.Properties.of().noLootTable()));
		blackInkBlockItem = REGISTER_ITEMS.register("black_ink", () -> new BlockItem(blackInk.get(), new Item.Properties()));

		BaseFlowingFluid.Properties whiteInkProperties = new BaseFlowingFluid.Properties(() -> whiteInkFluidType.get(), () -> whiteInkFluidStill.get(), () -> whiteInkFluidFlowing.get())
			.block(() -> (LiquidBlock) whiteInk.get())
			.bucket(() -> whiteVegetableOilInk.get());
		whiteVegetableOilInk = REGISTER_ITEMS.register("vegetable_oil_ink_white", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE)));
		whiteInkFluidType = REGISTER_FLUIDTYPES.register("white_ink", () -> new FluidType(FluidType.Properties.create()));
		whiteInkFluidStill = REGISTER_FLUIDS.register("white_ink", () -> new BaseFlowingFluid.Source(whiteInkProperties));
		whiteInkFluidFlowing = REGISTER_FLUIDS.register("white_ink_flowing", () -> new BaseFlowingFluid.Flowing(whiteInkProperties));
		whiteInk = REGISTER_BLOCKS.register("white_ink", () -> new LiquidBlock(() -> (FlowingFluid) whiteInkFluidStill.get(), BlockBehaviour.Properties.of().noLootTable()));
		whiteInkBlockItem = REGISTER_ITEMS.register("white_ink", () -> new BlockItem(whiteInk.get(), new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.DYES_BLACK).add(blackVegetableOilInk.get());
		provider.tagW(ContentHelper.ItemTags.DYES_WHITE).add(whiteVegetableOilInk.get());

		provider.tagW(ContentHelper.ItemTags.PIGMENT_BLACK).add(Items.CHARCOAL);
		provider.tagW(ContentHelper.ItemTags.PIGMENT_WHITE).add(Items.QUARTZ);

		provider.tagW(ContentHelper.ItemTags.INK_BLACK)
			.add(Items.INK_SAC)
			.add(blackVegetableOilInk.get());

		provider.tagW(ContentHelper.ItemTags.WAX).add(waxVegetable.get());
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
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, whiteVegetableOilInk.get())
			.requires(ContentHelper.ItemTags.VEGETABLE_OIL)
			.requires(ContentHelper.ItemTags.WAX)
			.requires(ContentHelper.ItemTags.ROSIN)
			.requires(ContentHelper.ItemTags.PIGMENT_WHITE)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(Ingredient.of(Items.INK_SAC), Ingredient.of(ContentHelper.ItemTags.INK_BLACK));

		Modifiers.crafting.addInputsToRemoveForOutput(blackVegetableOilInk.get(), ContentHelper.ItemTags.VEGETABLE_OIL);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(blackInkFluidStill.get(), FluidType.BUCKET_VOLUME), new FluidStack(VegetableOil.fluidVegetableOilStill.get(), FluidType.BUCKET_VOLUME), new InputItemStack(ContentHelper.ItemTags.WAX), new InputItemStack(ContentHelper.ItemTags.ROSIN), new InputItemStack(ContentHelper.ItemTags.PIGMENT_BLACK)));

		Modifiers.crafting.addInputsToRemoveForOutput(whiteVegetableOilInk.get(), ContentHelper.ItemTags.VEGETABLE_OIL);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(whiteInkFluidStill.get(), FluidType.BUCKET_VOLUME), new FluidStack(VegetableOil.fluidVegetableOilStill.get(), FluidType.BUCKET_VOLUME), new InputItemStack(ContentHelper.ItemTags.WAX), new InputItemStack(ContentHelper.ItemTags.ROSIN), new InputItemStack(ContentHelper.ItemTags.PIGMENT_WHITE)));

		RelationshipRegistry.addRelationship(whiteVegetableOilInk.get(), whiteInkBlockItem.get());
		RelationshipRegistry.addRelationship(whiteInkBlockItem.get(), whiteVegetableOilInk.get());
		RelationshipRegistry.addRelationship(blackVegetableOilInk.get(), blackInkBlockItem.get());
		RelationshipRegistry.addRelationship(blackInkBlockItem.get(), blackVegetableOilInk.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(blackVegetableOilInk.get());
		provider.basicItem(whiteVegetableOilInk.get());
		provider.basicItem(waxVegetable.get());
	}
}
