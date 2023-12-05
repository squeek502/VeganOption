package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockCompost;
import squeek.veganoption.blocks.BlockComposter;
import squeek.veganoption.blocks.renderers.RenderComposter;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.loot.GenericBlockLootSubProvider;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.gui.ComposterMenu;

import java.util.List;

import static squeek.veganoption.VeganOption.*;

public class Composting implements IContentModule
{
	public static RegistryObject<Block> composter;
	public static RegistryObject<Item> composterItem;
	public static RegistryObject<BlockEntityType<TileEntityComposter>> composterEntityType;
	public static RegistryObject<MenuType<ComposterMenu>> composterMenuType;
	public static RegistryObject<Item> rottenPlants;
	public static RegistryObject<Block> compost;
	public static RegistryObject<Item> compostItem;
	public static RegistryObject<Item> fertilizer;

	private static final String TEXTURE = ModInfo.MODID_LOWER + ":/entity/composter_legs";

	private static final FoodProperties ROTTEN_PLANTS_FOOD = new FoodProperties.Builder()
		.nutrition(4)
		.saturationMod(0.1f)
		.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8F)
		.meat()
		.build();

	@Override
	public void create()
	{
		composter = REGISTER_BLOCKS.register("composter", BlockComposter::new);
		composterItem = REGISTER_ITEMS.register("composter", () -> new BlockItem(composter.get(), new Item.Properties()));
		composterEntityType = REGISTER_BLOCKENTITIES.register("composter", () -> BlockEntityType.Builder.of(TileEntityComposter::new, composter.get()).build(null));
		composterMenuType = REGISTER_MENUS.register("composter", () -> IMenuTypeExtension.create((id, inv, data) -> new ComposterMenu(id, inv, data.readBlockPos())));

		rottenPlants = REGISTER_ITEMS.register("rotten_plants", () -> new Item(new Item.Properties().food(ROTTEN_PLANTS_FOOD)));
		fertilizer = REGISTER_ITEMS.register("fertilizer", () -> new BoneMealItem(new Item.Properties()));

		compost = REGISTER_BLOCKS.register("compost", BlockCompost::new);
		compostItem = REGISTER_ITEMS.register("compost", () -> new BlockItem(compost.get(), new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.ROTTEN_MATERIAL)
			.add(Items.ROTTEN_FLESH)
			.add(rottenPlants.get());
		provider.tagW(ContentHelper.ItemTags.DYES_BROWN).add(fertilizer.get());

		// Presently, no tags for fish or meat. Hopefully after Neoforged/Neoforge#135 there will be.
		provider.tagW(ContentHelper.ItemTags.FOOD_COOKED_FISH)
			.add(Items.COOKED_COD)
			.add(Items.COOKED_SALMON);
		provider.tagW(ContentHelper.ItemTags.FOOD_RAW_FISH)
			.add(Items.COD)
			.add(Items.SALMON)
			.add(Items.PUFFERFISH);

		provider.tagW(ContentHelper.ItemTags.FOOD_COOKED_MEAT)
			.add(Items.COOKED_BEEF)
			.add(Items.COOKED_CHICKEN)
			.add(Items.COOKED_MUTTON)
			.add(Items.COOKED_PORKCHOP)
			.add(Items.COOKED_RABBIT);
		provider.tagW(ContentHelper.ItemTags.FOOD_RAW_MEAT)
			.add(Items.BEEF)
			.add(Items.CHICKEN)
			.add(Items.MUTTON)
			.add(Items.PORKCHOP)
			.add(Items.RABBIT);

		provider.tagW(ContentHelper.ItemTags.FOOD)
			.addTag(ContentHelper.ItemTags.FOOD_COOKED_FISH)
			.addTag(ContentHelper.ItemTags.FOOD_RAW_FISH)
			.addTag(ContentHelper.ItemTags.FOOD_COOKED_MEAT)
			.addTag(ContentHelper.ItemTags.FOOD_RAW_MEAT);
	}

	@Override
	public void datagenBlockTags(DataGenProviders.BlockTags provider)
	{
		provider.tagW(BlockTags.MINEABLE_WITH_SHOVEL).add(compost.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, composterItem.get())
			.pattern("/c/")
			.pattern("/ /")
			.pattern(" / ")
			.define('/', ContentHelper.ItemTags.STICKS)
			.define('c', Items.CHEST)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, fertilizer.get(), 8)
			.requires(compostItem.get())
			.requires(ContentHelper.ItemTags.SALTPETER)
			.unlockedBy("has_compost", provider.hasW(compostItem.get()))
			.save(output);
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(ContentHelper.ItemTags.ROTTEN_MATERIAL));
		RelationshipRegistry.addRelationship(compostItem.get(), composterItem.get());
		RelationshipRegistry.addRelationship(rottenPlants.get(), composterItem.get());

		CompostRegistry.addBrown(ContentHelper.ItemTags.STICKS);
		CompostRegistry.addBrown(Items.PAPER);
		CompostRegistry.addBrown(ContentHelper.ItemTags.FIBRES);
		CompostRegistry.addBrown(ContentHelper.ItemTags.DUST_WOOD);
		CompostRegistry.addBrown(Items.DEAD_BUSH);

		CompostRegistry.addGreen(ContentHelper.ItemTags.SAPLINGS);
		CompostRegistry.addGreen(rottenPlants.get());
		CompostRegistry.addGreen(Items.TALL_GRASS);
		CompostRegistry.addGreen(Items.FERN);
		CompostRegistry.addGreen(Items.LARGE_FERN);
		CompostRegistry.addGreen(ContentHelper.ItemTags.LEAVES);
		CompostRegistry.addGreen(Items.PUMPKIN);
		CompostRegistry.addGreen(Items.MELON);
		CompostRegistry.addGreen(Items.VINE);
		CompostRegistry.addGreen(ContentHelper.ItemTags.FLOWERS);
		CompostRegistry.addGreen(Items.BROWN_MUSHROOM);
		CompostRegistry.addGreen(Items.RED_MUSHROOM);

		CompostRegistry.blacklist(new FoodSpecifier()
		{
			@Override
			public boolean matches(ItemStack itemStack)
			{
				if (itemStack.isEdible())
				{
					if (itemStack.getFoodProperties(null).isMeat())
						return true;
					if (ContentHelper.isItemTaggedAs(itemStack.getItem(), ContentHelper.ItemTags.FOOD_RAW_FISH) || ContentHelper.isItemTaggedAs(itemStack.getItem(), ContentHelper.ItemTags.FOOD_COOKED_FISH))
						return true;
					if (ContentHelper.isItemTaggedAs(itemStack.getItem(), ContentHelper.ItemTags.FOOD_RAW_MEAT) || ContentHelper.isItemTaggedAs(itemStack.getItem(), ContentHelper.ItemTags.FOOD_COOKED_MEAT))
						return true;
				}

				return false;
			}
		});

		CompostRegistry.registerAllFoods();
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.simpleBlock(compost.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.withExistingParent("composter", provider.modLoc("composter"));
		provider.basicItem(rottenPlants.get());
		provider.basicItem(fertilizer.get());
		provider.withExistingParent("compost", provider.modLoc("compost"));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(composterEntityType.get(), RenderComposter::new);
	}

	@Override
	public BlockLootSubProvider getBlockLootProvider()
	{
		return new GenericBlockLootSubProvider() {
			@Override
			protected void generate()
			{
				dropSelf(compost.get());
				dropSelf(composter.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return List.of(compost.get(), composter.get());
			}
		};
	}
}
