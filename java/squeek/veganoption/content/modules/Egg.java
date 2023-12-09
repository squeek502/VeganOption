package squeek.veganoption.content.modules;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.EggModifier;
import squeek.veganoption.content.recipes.EggRecipe;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.entities.EntityPlasticEgg;
import squeek.veganoption.items.ItemPlasticEgg;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.*;

public class Egg implements IContentModule
{
	public static RegistryObject<Item> potatoStarch;
	public static RegistryObject<Item> appleSauce;
	public static RegistryObject<Item> plasticEgg;
	public static RegistryObject<EntityType<EntityPlasticEgg>> plasticEggEntityType;
	public static RegistryObject<RecipeSerializer<EggRecipe>> eggRecipeSerializer;

	@Override
	public void create()
	{
		appleSauce = REGISTER_ITEMS.register("apple_sauce", () -> new BowlFoodItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(1f).build())));
		potatoStarch = REGISTER_ITEMS.register("potato_starch", () -> new Item(new Item.Properties()));
		plasticEgg = REGISTER_ITEMS.register("plastic_egg", ItemPlasticEgg::new);

		plasticEggEntityType = REGISTER_ENTITIES.register("plastic_egg", () -> EntityType.Builder.<EntityPlasticEgg>of(EntityPlasticEgg::new, MobCategory.MISC)
			.sized(0.25f, 0.25f)
			.updateInterval(1)
			.setTrackingRange(80)
			.setShouldReceiveVelocityUpdates(true)
			.build(new ResourceLocation(MODID_LOWER, "plastic_egg").toString()));

		eggRecipeSerializer = REGISTER_RECIPESERIALIZERS.register("plastic_egg_recipe", () -> new SimpleCraftingRecipeSerializer<>(EggRecipe::new));
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(appleSauce.get());
		provider.basicItem(potatoStarch.get());
		provider.basicItem(plasticEgg.get());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(plasticEggEntityType.get(), ThrownItemRenderer::new);
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.EGG_OBJECT)
			.addTag(ContentHelper.ItemTags.EGGS)
			.add(plasticEgg.get());
		provider.tagW(ContentHelper.ItemTags.EGG_BAKING)
			.addTag(ContentHelper.ItemTags.EGGS)
			.add(appleSauce.get())
			.add(potatoStarch.get());
		provider.tagW(ContentHelper.ItemTags.STARCH).add(potatoStarch.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, appleSauce.get())
			.requires(Ingredient.of(Items.APPLE))
			.requires(Ingredient.of(Items.BOWL))
			.unlockedBy("has_bowl", provider.hasW(Items.BOWL))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, plasticEgg.get())
			.pattern(" o ")
			.pattern("o o")
			.pattern(" o ")
			.define('o', ContentHelper.ItemTags.PLASTIC_SHEET)
			.unlockedBy("has_bioplastic", provider.hasW(Bioplastic.bioplastic.get()))
			.save(output);

		SpecialRecipeBuilder.special(eggRecipeSerializer.get()).save(output, new ResourceLocation(MODID_LOWER, "plastic_egg_storage"));
 	}

	@Override
	public void finish()
	{
		PistonCraftingRegistry.register(new PistonCraftingRecipe(new ItemStack(potatoStarch.get()), new InputItemStack(Items.POTATO)));

		Modifiers.eggs.addItem(Items.GUNPOWDER, new EggModifier()
		{
			@Override
			public void onHitGeneric(HitResult hitResult, EntityPlasticEgg eggEntity)
			{
				eggEntity.level().explode(eggEntity.getOwner(), eggEntity.getBlockX(), eggEntity.getBlockY(), eggEntity.getBlockZ(), 2F, Level.ExplosionInteraction.BLOCK);
			}
		});
		EggModifier growModifier = new EggModifier()
		{
			@Override
			public void onHitBlock(BlockHitResult hitResult, EntityPlasticEgg eggEntity)
			{
				Level level = eggEntity.level();
				if (level.isClientSide())
					return;
				BlockPos posToGrow = hitResult.getBlockPos();
				BlockState stateToGrow = level.getBlockState(posToGrow);
				Block blockToGrow = stateToGrow.getBlock();
				if (blockToGrow instanceof BonemealableBlock bonemealable && bonemealable.isValidBonemealTarget(level, posToGrow, stateToGrow) &&
					level instanceof ServerLevel serverLevel && bonemealable.isBonemealSuccess(level, level.random, posToGrow, stateToGrow))
					bonemealable.performBonemeal(serverLevel, level.random, posToGrow, stateToGrow);
			}
		};
		Modifiers.eggs.addItem(Items.BONE_MEAL, growModifier);
		// todo: compat module
		Modifiers.eggs.addItem(Composting.fertilizer.get(), growModifier);

		Modifiers.recipes.convertInputForFood(() -> Ingredient.of(Items.EGG), () -> Ingredient.of(ContentHelper.ItemTags.EGG_BAKING));
		Modifiers.recipes.convertInputForFood(() -> Ingredient.of(ContentHelper.ItemTags.EGGS), () -> Ingredient.of(ContentHelper.ItemTags.EGG_BAKING));
		Modifiers.recipes.convertInputForNonFood(() -> Ingredient.of(Items.EGG), () -> Ingredient.of(ContentHelper.ItemTags.EGG_OBJECT));
		Modifiers.recipes.convertInputForNonFood(() -> Ingredient.of(ContentHelper.ItemTags.EGGS), () -> Ingredient.of(ContentHelper.ItemTags.EGG_OBJECT));
	}
}
