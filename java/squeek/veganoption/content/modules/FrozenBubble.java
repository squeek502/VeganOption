package squeek.veganoption.content.modules;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.crafting.StrictNBTIngredient;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.recipes.ShapelessDamageItemRecipeBuilder;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.fluids.GenericFluidTypeRenderProperties;
import squeek.veganoption.items.GenericBucketItem;
import squeek.veganoption.items.ItemFrozenBubble;
import squeek.veganoption.items.ItemSoapSolution;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.*;

public class FrozenBubble implements IContentModule
{
	public static Supplier<Item> soapSolution;
	public static Supplier<Item> soapSolutionBucket;
	public static DeferredHolder<Item, ItemFrozenBubble> frozenBubble;
	public static Supplier<EntityType<EntityBubble>> bubbleEntityType;
	public static Supplier<FluidType> soapSolutionFluidType;
	public static Supplier<Fluid> soapSolutionStill;
	public static Supplier<Fluid> soapSolutionFlowing;
	public static Supplier<Block> soapSolutionBlock;

	@Override
	public void create()
	{
		BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(() -> soapSolutionFluidType.get(), () -> soapSolutionStill.get(), () -> soapSolutionFlowing.get())
			.block(() -> (LiquidBlock) soapSolutionBlock.get())
			.bucket(() -> soapSolutionBucket.get());

		soapSolutionFluidType = REGISTER_FLUIDTYPES.register("soap_solution", () -> new FluidType(FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
			{
				consumer.accept(new GenericFluidTypeRenderProperties("soap_solution", 0x1f55ff));
			}
		});
		soapSolutionStill = REGISTER_FLUIDS.register("soap_solution", () -> new BaseFlowingFluid.Source(fluidProperties));
		soapSolutionFlowing = REGISTER_FLUIDS.register("soap_solution_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties));
		soapSolutionBlock = REGISTER_BLOCKS.register("soap_solution", () -> new LiquidBlock(() -> (FlowingFluid) soapSolutionStill.get(), BlockBehaviour.Properties.of()
			.noLootTable()
			.mapColor(MapColor.NONE)
			.replaceable()
			.noCollission()
			.strength(100f)
			.pushReaction(PushReaction.DESTROY)
			.liquid()));

		soapSolution = REGISTER_ITEMS.register("soap_solution", ItemSoapSolution::new);
		soapSolutionBucket = REGISTER_ITEMS.register("soap_solution_bucket", () -> new GenericBucketItem(() -> soapSolutionStill.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

		frozenBubble = REGISTER_ITEMS.register("frozen_bubble", ItemFrozenBubble::new);
		bubbleEntityType = REGISTER_ENTITIES.register("bubble", () -> EntityType.Builder.<EntityBubble>of(EntityBubble::new, MobCategory.MISC)
			.updateInterval(80)
			.setTrackingRange(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(new ResourceLocation(MODID_LOWER, "bubble").toString()));
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(soapSolution.get());
		provider.basicItem(soapSolutionBucket.get());

		ResourceLocation damagedPredicate = new ResourceLocation("damaged");
		ItemModelBuilder emptyModel = provider.basicItem(frozenBubble.get());
		ItemModelBuilder partiallyFilledModel = provider.basicItem(provider.modLoc(frozenBubble.getId().getPath() + "_filled"));
		// todo: maybe make it gradually fill up
		emptyModel
			.override()
			.predicate(damagedPredicate, 0)
			.model(emptyModel)
			.end()
			.override()
			.predicate(damagedPredicate, 1)
			.model(partiallyFilledModel)
			.end();
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.getVariantBuilder(soapSolutionBlock.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(provider.models().getExistingFile(provider.modLoc("soap_solution"))).build());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(bubbleEntityType.get(), ThrownItemRenderer::new);
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.REAGENT_WATERBREATHING)
			.add(Items.PUFFERFISH)
			.add(frozenBubble.get());
	}

	@Override
	public void datagenFluidTags(DataGenProviders.FluidTags provider)
	{
		provider.tagW(ContentHelper.FluidTags.SOAP_SOLUTION)
			.add(soapSolutionStill.get())
			.add(soapSolutionFlowing.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessDamageItemRecipeBuilder.shapeless(RecipeCategory.MISC, soapSolution.get())
			.requires(ContentHelper.ItemTags.SOAP)
			.requires(Items.SUGAR)
			.requires(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)))
			.unlockedBy("has_soap", provider.hasW(Soap.soap.get()))
			.save(output);
		// the following bulk recipe must be done with an undamaged soap.
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, soapSolutionBucket.get())
				.requires(StrictNBTIngredient.of(new ItemStack(Soap.soap.get())))
				.requires(Items.SUGAR)
				.requires(Items.SUGAR)
				.requires(Items.SUGAR)
				.requires(Items.SUGAR)
				.requires(Items.WATER_BUCKET)
				.unlockedBy("has_soap", provider.hasW(Soap.soap.get()))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, frozenBubble.get())
			.pattern("iii")
			.pattern("isi")
			.pattern("iii")
			.define('i', Items.ICE)
			.define('s', soapSolution.get())
			.unlockedBy("has_soap_solution", provider.hasW(soapSolution.get()))
			.save(output, new ResourceLocation(ModInfo.MODID_LOWER, "frozen_bubble_ice"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, frozenBubble.get())
			.requires(Items.PACKED_ICE)
			.requires(soapSolution.get())
			.unlockedBy("has_soap_solution", provider.hasW(soapSolution.get()))
			.save(output, new ResourceLocation(ModInfo.MODID_LOWER, "frozen_bubble_packed_ice"));
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.PUFFERFISH), () -> Ingredient.of(ContentHelper.ItemTags.REAGENT_WATERBREATHING));

		Modifiers.crafting.addInputsToRemoveForOutput(soapSolutionBucket.get(), () -> new Ingredient[] { Ingredient.of(Items.WATER_BUCKET) });

		Modifiers.bottles.registerCustomBottleHandler(ContentHelper.FluidTags.SOAP_SOLUTION, () -> new ItemStack(soapSolution.get()), (stack) -> stack.getItem() == soapSolution.get() && !stack.isDamaged(), () -> soapSolutionStill.get());

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new FluidStack(soapSolutionStill.get(), FluidType.BUCKET_VOLUME), new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), new InputItemStack(new ItemStack(Items.SUGAR, 4)), new InputItemStack(ContentHelper.ItemTags.SOAP)));

		RelationshipRegistry.addRelationship(frozenBubble.get(), soapSolution.get());
		RelationshipRegistry.addRelationship(Items.ENDER_PEARL, frozenBubble.get());

		PotionBrewing.addMix(Potions.AWKWARD, frozenBubble.get(), Potions.WATER_BREATHING);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void finishClient(FMLClientSetupEvent event)
	{
		ItemBlockRenderTypes.setRenderLayer(soapSolutionStill.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(soapSolutionFlowing.get(), RenderType.translucent());
	}
}
