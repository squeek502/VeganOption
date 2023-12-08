package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.recipes.ShapelessMatchingTagRecipeBuilder;
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.content.registry.PistonCraftingRegistry;

import static squeek.veganoption.VeganOption.*;

public class PlantMilk implements IContentModule
{
	public static RegistryObject<FluidType> plantMilkFluidType;
	public static RegistryObject<Fluid> plantMilkStill;
	public static RegistryObject<Fluid> plantMilkFlowing;
	public static RegistryObject<Block> plantMilkBlock;
	public static RegistryObject<Item> plantMilkBucket;

	@Override
	public void create()
	{
		BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(() -> plantMilkFluidType.get(), () -> plantMilkStill.get(), () -> plantMilkFlowing.get())
			.block(() -> (LiquidBlock) plantMilkBlock.get())
			.bucket(() -> plantMilkBucket.get());
		plantMilkFluidType = REGISTER_FLUIDTYPES.register("plant_milk", () -> new FluidType(FluidType.Properties.create()));
		plantMilkStill = REGISTER_FLUIDS.register("plant_milk", () -> new BaseFlowingFluid.Source(fluidProperties));
		plantMilkFlowing = REGISTER_FLUIDS.register("plant_milk_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties));
		plantMilkBlock = REGISTER_BLOCKS.register("plant_milk", () -> new LiquidBlock(() -> (FlowingFluid) plantMilkStill.get(), BlockBehaviour.Properties.of().noLootTable()));
		plantMilkBucket = REGISTER_ITEMS.register("plant_milk_bucket", () -> new BucketItem(() -> plantMilkStill.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		// todo: mod nuts, soybeans, oats, rice, etc.
		provider.tagW(ContentHelper.ItemTags.PLANT_MILK_SOURCES).add(Items.PUMPKIN_SEEDS);
		provider.tagW(ContentHelper.ItemTags.MILK)
			.add(Items.MILK_BUCKET)
			.add(plantMilkBucket.get());
	}

	@Override
	public void datagenFluidTags(DataGenProviders.FluidTags provider)
	{
		provider.tagW(ContentHelper.FluidTags.MILK)
			.add(plantMilkStill.get())
			.add(plantMilkFlowing.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessMatchingTagRecipeBuilder.shapeless(RecipeCategory.FOOD, plantMilkBucket.get())
			.requires(Items.WATER_BUCKET)
			.requires(Ingredient.of(ContentHelper.ItemTags.PLANT_MILK_SOURCES), 2)
			.requires(Items.SUGAR)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);
	}

	@Override
	public void finish()
	{
		//RelationshipRegistry.addRelationship(bucketPlantMilk.copy(), new ItemStack(plantMilk));
		//RelationshipRegistry.addRelationship(new ItemStack(plantMilk), bucketPlantMilk.copy());
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.MILK_BUCKET), () -> Ingredient.of(ContentHelper.ItemTags.MILK));

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(plantMilkStill.get(), FluidType.BUCKET_VOLUME), new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), new InputItemStack(Items.SUGAR), new InputItemStack(ContentHelper.ItemTags.PLANT_MILK_SOURCES, 2)));

		DescriptionRegistry.registerCustomCraftingText(plantMilkBucket.get(), ModInfo.MODID_LOWER + ":plant_milk_bucket.vowiki.crafting");
		DescriptionRegistry.registerCustomUsageText(plantMilkBucket.get(), ModInfo.MODID_LOWER + ":plant_milk_bucket.vowiki.usage");
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.withExistingParent(plantMilkBucket.getId().getPath(), provider.mcLoc("milk_bucket"));
	}
}
