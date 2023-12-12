package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
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
import squeek.veganoption.fluids.GenericFluidTypeRenderProperties;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.loot.ReplaceLootModifier;

import java.util.function.Consumer;

import static squeek.veganoption.VeganOption.*;

public class VegetableOil implements IContentModule
{
	public static RegistryObject<Item> seedSunflower;
	public static RegistryObject<Item> vegetableOilBottle;
	public static RegistryObject<Item> vegetableOilBucket;
	public static RegistryObject<FluidType> fluidTypeVegetableOil;
	public static RegistryObject<Fluid> fluidVegetableOilStill;
	public static RegistryObject<Fluid> fluidVegetableOilFlowing;
	public static RegistryObject<Block> fluidBlockVegetableOil;

	@Override
	public void create()
	{
		seedSunflower = REGISTER_ITEMS.register("sunflower_seeds", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.05f).build())));

		BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(() -> fluidTypeVegetableOil.get(), () -> fluidVegetableOilStill.get(), () -> fluidVegetableOilFlowing.get())
			.block(() -> (LiquidBlock) fluidBlockVegetableOil.get())
			.bucket(() -> vegetableOilBucket.get());
		vegetableOilBottle = REGISTER_ITEMS.register("vegetable_oil", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE)));
		fluidTypeVegetableOil = REGISTER_FLUIDTYPES.register("vegetable_oil", () ->
			new FluidType(FluidType.Properties
				.create()
				.density(900)
				.viscosity(1100)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
					@Override
					public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
					{
						consumer.accept(new GenericFluidTypeRenderProperties("vegetable_oil", 0xEFC600));
					}
		});
		fluidVegetableOilStill = REGISTER_FLUIDS.register("vegetable_oil", () -> new BaseFlowingFluid.Source(fluidProperties));
		fluidVegetableOilFlowing = REGISTER_FLUIDS.register("vegetable_oil_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties));
		fluidBlockVegetableOil = REGISTER_BLOCKS.register("vegetable_oil", () -> new LiquidBlock(() -> (FlowingFluid) fluidVegetableOilStill.get(), BlockBehaviour.Properties.of()
			.noLootTable()
			.mapColor(DyeColor.YELLOW)
			.replaceable()
			.noCollission()
			.strength(100f)
			.pushReaction(PushReaction.DESTROY)
			.liquid()));
		vegetableOilBucket = REGISTER_ITEMS.register("vegetable_oil_bucket", () -> new BucketItem(() -> fluidVegetableOilStill.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.SEEDS_SUNFLOWER).add(seedSunflower.get());
		provider.tagW(ContentHelper.ItemTags.SEEDS).addTag(ContentHelper.ItemTags.SEEDS_SUNFLOWER);
		provider.tagW(ContentHelper.ItemTags.VEGETABLE_OIL).add(vegetableOilBottle.get());
		provider.tagW(ContentHelper.ItemTags.VEGETABLE_OIL_SOURCES).add(seedSunflower.get());
	}

	@Override
	public void datagenFluidTags(DataGenProviders.FluidTags provider)
	{
		provider.tagW(ContentHelper.FluidTags.VEGETABLE_OIL)
			.add(fluidVegetableOilStill.get())
			.add(fluidVegetableOilFlowing.get());
	}

	@Override
	public void finish()
	{
		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(fluidVegetableOilStill.get(), FluidHelper.MB_PER_BOTTLE), new InputItemStack(ContentHelper.ItemTags.VEGETABLE_OIL_SOURCES)));

		Modifiers.bottles.registerCustomBottleHandler(ContentHelper.FluidTags.VEGETABLE_OIL, () -> new ItemStack(vegetableOilBottle.get()), (stack) -> stack.getItem() == vegetableOilBottle.get(), fluidVegetableOilStill);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void finishClient(FMLClientSetupEvent event)
	{
		ItemBlockRenderTypes.setRenderLayer(fluidVegetableOilStill.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(fluidVegetableOilFlowing.get(), RenderType.translucent());
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.getVariantBuilder(fluidBlockVegetableOil.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(provider.models().getExistingFile(provider.modLoc("vegetable_oil"))).build());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(seedSunflower.get());
		provider.basicItem(vegetableOilBottle.get());
		provider.basicItem(vegetableOilBucket.get());
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
