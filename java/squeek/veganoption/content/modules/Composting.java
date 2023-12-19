package squeek.veganoption.content.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockCompost;
import squeek.veganoption.blocks.BlockComposter;
import squeek.veganoption.blocks.renderers.RenderComposter;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.gui.ComposterMenu;
import squeek.veganoption.gui.ComposterScreen;
import squeek.veganoption.loot.GenericBlockLootSubProvider;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModInfo.MODID_LOWER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Composting implements IContentModule
{
	public static Supplier<Block> composter;
	public static Supplier<Item> composterItem;
	public static Supplier<BlockEntityType<TileEntityComposter>> composterEntityType;
	public static Supplier<MenuType<ComposterMenu>> composterMenuType;
	public static Supplier<Item> rottenPlants;
	public static Supplier<Block> compost;
	public static Supplier<Item> compostItem;
	public static Supplier<Item> fertilizer;

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
		composterItem = REGISTER_ITEMS.register("composter", () -> new BlockItem(composter.get(), new Item.Properties()) {
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				consumer.accept(new IClientItemExtensions() {
					@Override
					public BlockEntityWithoutLevelRenderer getCustomRenderer()
					{
						Minecraft mc = Minecraft.getInstance();
						return new RenderComposter.ComposterItemRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
					}
				});
			}
		});
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
			.add(Items.PUFFERFISH)
			.add(Items.TROPICAL_FISH);

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

		// Compostables
		provider.tagW(ContentHelper.ItemTags.COMPOSTABLES_BROWN)
			.addTag(ContentHelper.ItemTags.STICKS)
			.addTag(ContentHelper.ItemTags.FIBRES)
			.add(Items.PAPER)
			.add(Items.DEAD_BUSH)
			.addOptionalTag(ContentHelper.ItemTags.DUST_WOOD.location());

		provider.tagW(ContentHelper.ItemTags.COMPOSTABLES_GREEN)
			.addTag(ContentHelper.ItemTags.SAPLINGS)
			.addTag(ContentHelper.ItemTags.LEAVES)
			.addTag(ContentHelper.ItemTags.FLOWERS)
			.add(rottenPlants.get())
			.add(Items.TALL_GRASS)
			.add(Items.GRASS)
			.add(Items.FERN)
			.add(Items.LARGE_FERN)
			.add(Items.PUMPKIN)
			.add(Items.MELON)
			.add(Items.VINE)
			.add(Items.BROWN_MUSHROOM)
			.add(Items.RED_MUSHROOM)
			.add(Items.KELP);

		provider.tagW(ContentHelper.ItemTags.COMPOSTABLES_BLACKLIST)
			.addTag(ContentHelper.ItemTags.FOOD_RAW_MEAT)
			.addTag(ContentHelper.ItemTags.FOOD_COOKED_MEAT)
			.addTag(ContentHelper.ItemTags.FOOD_RAW_FISH)
			.addTag(ContentHelper.ItemTags.FOOD_COOKED_FISH)
			.add(Items.SUSPICIOUS_STEW)
			.add(Items.GOLDEN_APPLE)
			.add(Items.ENCHANTED_GOLDEN_APPLE)
			.add(Items.GOLDEN_CARROT)
			.add(Items.SPIDER_EYE);
	}

	@Override
	public void datagenBlockTags(DataGenProviders.BlockTags provider)
	{
		provider.tagW(BlockTags.MINEABLE_WITH_SHOVEL).add(compost.get());
		provider.tagW(BlockTags.MINEABLE_WITH_AXE).add(composter.get());
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
			.unlockedBy("has_chest", provider.hasW(Items.CHEST))
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
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.ROTTEN_FLESH), () -> Ingredient.of(ContentHelper.ItemTags.ROTTEN_MATERIAL));
		RelationshipRegistry.addRelationship(compostItem.get(), composterItem.get());
		RelationshipRegistry.addRelationship(rottenPlants.get(), composterItem.get());
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		provider.simpleBlock(compost.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(rottenPlants.get());
		provider.basicItem(fertilizer.get());
		provider.withExistingParent("compost", provider.modLoc("block/compost"));
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

	@Override
	public void finishClient(FMLClientSetupEvent event)
	{
		MenuScreens.register(composterMenuType.get(), ComposterScreen::new);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerComposterLegsModel(ModelEvent.RegisterAdditional event)
	{
		event.register(RenderComposter.LEGS_MODEL);
	}
}
