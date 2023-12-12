package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
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
import squeek.veganoption.fluids.GenericFluidTypeRenderProperties;

import java.util.function.Consumer;

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
		plantMilkFluidType = REGISTER_FLUIDTYPES.register("plant_milk", () ->
			new FluidType(FluidType.Properties.create()
				.density(1024)
				.viscosity(1024)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
					@Override
					public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
					{
						consumer.accept(new GenericFluidTypeRenderProperties("plant_milk", 0xFFFFFF));
					}
		});
		plantMilkStill = REGISTER_FLUIDS.register("plant_milk", () -> new BaseFlowingFluid.Source(fluidProperties));
		plantMilkFlowing = REGISTER_FLUIDS.register("plant_milk_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties));
		plantMilkBlock = REGISTER_BLOCKS.register("plant_milk", () -> new LiquidBlock(() -> (FlowingFluid) plantMilkStill.get(), BlockBehaviour.Properties.of()
			.noLootTable()
			.mapColor(DyeColor.WHITE)
			.replaceable()
			.noCollission()
			.strength(100f)
			.pushReaction(PushReaction.DESTROY)
			.liquid()));
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

	@OnlyIn(Dist.CLIENT)
	@Override
	public void finishClient(FMLClientSetupEvent event)
	{
		ItemBlockRenderTypes.setRenderLayer(plantMilkStill.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(plantMilkFlowing.get(), RenderType.translucent());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.withExistingParent(plantMilkBucket.getId().getPath(), provider.mcLoc("milk_bucket"));
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.getVariantBuilder(plantMilkBlock.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(provider.models().getExistingFile(provider.modLoc("plant_milk"))).build());
	}
}
