package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.ObjectHolder;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.loot.ReplaceLootModifier;

import static squeek.veganoption.VeganOption.*;

public class VegetableOil implements IContentModule
{
	public static RegistryObject<Item> seedSunflower;
	public static RegistryObject<Item> oilVegetable;
	public static RegistryObject<FluidType> fluidTypeVegetableOil;
	public static RegistryObject<Fluid> fluidVegetableOilStill;
	public static RegistryObject<Fluid> fluidVegetableOilFlowing;
	public static RegistryObject<Block> fluidBlockVegetableOil;

	@ObjectHolder(registryName = "minecraft:item", value = "minecraft:heavy_weighted_pressure_plate")
	public static Item oilPresser;

	@Override
	public void create()
	{
		seedSunflower = REGISTER_ITEMS.register("sunflower_seeds", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.05f).build())));

		BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(() -> fluidTypeVegetableOil.get(), () -> fluidVegetableOilStill.get(), () -> fluidVegetableOilFlowing.get())
			.block(() -> (LiquidBlock) fluidBlockVegetableOil.get())
			.bucket(() -> oilVegetable.get());
		oilVegetable = REGISTER_ITEMS.register("vegetable_oil", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE)));
		fluidTypeVegetableOil = REGISTER_FLUIDTYPES.register("vegetable_oil", () -> new FluidType(FluidType.Properties.create()));
		fluidVegetableOilStill = REGISTER_FLUIDS.register("vegetable_oil", () -> new BaseFlowingFluid.Source(fluidProperties));
		fluidVegetableOilFlowing = REGISTER_FLUIDS.register("vegetable_oil_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties));
		fluidBlockVegetableOil = REGISTER_BLOCKS.register("vegetable_oil", () -> new LiquidBlock(() -> (FlowingFluid) fluidVegetableOilStill.get(), BlockBehaviour.Properties.of().noLootTable()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.OIL_PRESSERS).add(oilPresser);
		provider.tagW(ContentHelper.ItemTags.SEEDS_SUNFLOWER).add(seedSunflower.get());
		provider.tagW(ContentHelper.ItemTags.SEEDS).addTag(ContentHelper.ItemTags.SEEDS_SUNFLOWER);
		provider.tagW(ContentHelper.ItemTags.VEGETABLE_OIL).add(oilVegetable.get());
		provider.tagW(ContentHelper.ItemTags.VEGETABLE_OIL_SOURCES).add(seedSunflower.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, oilVegetable.get())
			.requires(ContentHelper.ItemTags.OIL_PRESSERS)
			.requires(ContentHelper.ItemTags.VEGETABLE_OIL_SOURCES)
			.requires(Items.GLASS_BOTTLE)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);
	}

	@Override
	public void finish()
	{
		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(fluidVegetableOilStill.get(), FluidType.BUCKET_VOLUME), new InputItemStack(ContentHelper.ItemTags.VEGETABLE_OIL_SOURCES)));

		Modifiers.crafting.addInputsToKeepForOutput(oilVegetable.get(), oilPresser);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(seedSunflower.get());
		provider.basicItem(oilVegetable.get());
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add("sunflower_seeds", new ReplaceLootModifier(
			new LootItemCondition[] {
				new InvertedLootItemCondition(MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)).build()),
				new LootTableIdCondition.Builder(Tags.Blocks.STONE.location()).build()
			},
			Items.SUNFLOWER,
			seedSunflower.get()));
	}
}
