package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.items.ItemFrozenBubble;
import squeek.veganoption.items.ItemSoapSolution;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.*;

public class FrozenBubble implements IContentModule
{
	public static RegistryObject<Item> soapSolution;
	public static RegistryObject<Item> frozenBubble;
	public static RegistryObject<EntityType<EntityBubble>> bubbleEntityType;
	public static RegistryObject<FluidType> soapSolutionFluidType;
	public static RegistryObject<Fluid> soapSolutionStill;
	public static RegistryObject<Fluid> soapSolutionFlowing;
	public static RegistryObject<Block> soapSolutionBlock;
	public static RegistryObject<Item> soapSolutionBlockItem;

	@Override
	public void create()
	{
		BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(() -> soapSolutionFluidType.get(), () -> soapSolutionStill.get(), () -> soapSolutionFlowing.get())
			.block(() -> (LiquidBlock) soapSolutionBlock.get())
			.bucket(() -> soapSolution.get());

		soapSolutionFluidType = REGISTER_FLUIDTYPES.register("soap_solution", () -> new FluidType(FluidType.Properties.create()));
		soapSolutionStill = REGISTER_FLUIDS.register("soap_solution", () -> new BaseFlowingFluid.Source(fluidProperties));
		soapSolutionFlowing = REGISTER_FLUIDS.register("soap_solution_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties));
		soapSolutionBlock = REGISTER_BLOCKS.register("soap_solution", () -> new LiquidBlock(() -> (FlowingFluid) soapSolutionStill.get(), BlockBehaviour.Properties.of().noLootTable()));
		soapSolutionBlockItem = REGISTER_ITEMS.register("fluid_soap_solution", () -> new BlockItem(soapSolutionBlock.get(), new Item.Properties()));

		soapSolution = REGISTER_ITEMS.register("soap_solution", ItemSoapSolution::new);

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
		provider.basicItem(frozenBubble.get());
		provider.withExistingParent("fluid_soap_solution", provider.modLoc("soap_solution"));
	}

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
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, soapSolution.get())
			.requires(ContentHelper.ItemTags.SOAP)
			.requires(Items.WATER_BUCKET)
			.requires(Items.SUGAR)
			.requires(Items.GLASS_BOTTLE)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, frozenBubble.get())
			.pattern("iii")
			.pattern("isi")
			.pattern("iii")
			.define('i', Items.ICE)
			.define('s', soapSolution.get())
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output, new ResourceLocation(ModInfo.MODID_LOWER, "frozen_bubble_ice"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, frozenBubble.get())
			.requires(Items.PACKED_ICE)
			.requires(soapSolution.get())
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output, new ResourceLocation(ModInfo.MODID_LOWER, "frozen_bubble_packed_ice"));
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(Ingredient.of(Items.PUFFERFISH), Ingredient.of(ContentHelper.ItemTags.REAGENT_WATERBREATHING));

		Modifiers.crafting.addInputsToKeepForOutput(soapSolution.get(), ContentHelper.ItemTags.SOAP);

		RelationshipRegistry.addRelationship(frozenBubble.get(), soapSolution.get());
		RelationshipRegistry.addRelationship(Items.ENDER_PEARL, frozenBubble.get());
		RelationshipRegistry.addRelationship(soapSolution.get(), soapSolutionBlockItem.get());
		RelationshipRegistry.addRelationship(soapSolutionBlockItem.get(), soapSolution.get());
	}

}
