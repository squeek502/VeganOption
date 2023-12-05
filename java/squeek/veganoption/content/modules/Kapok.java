package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockKapok;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.loot.GenericBlockLootSubProvider;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.REGISTER_BLOCKS;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class Kapok implements IContentModule
{
	public static RegistryObject<Item> kapokTuft;
	public static Map<DyeColor, RegistryObject<Block>> kapokBlocks = new EnumMap<>(DyeColor.class);
	public static Map<DyeColor, RegistryObject<Item>> kapokBlockItems = new EnumMap<>(DyeColor.class);
	private static Map<DyeColor, Supplier<Item>> woolBlockItems = new EnumMap<>(DyeColor.class);

	@Override
	public void create()
	{
		kapokTuft = REGISTER_ITEMS.register("kapok_tuft", () -> new Item(new Item.Properties()));
		for (DyeColor color : DyeColor.values())
		{
			kapokBlocks.put(color, REGISTER_BLOCKS.register(color.getName() + "_kapok", () -> new BlockKapok(color)));
			kapokBlockItems.put(color, REGISTER_ITEMS.register(color.getName() + "_kapok", () -> new BlockItem(kapokBlocks.get(color).get(), new Item.Properties())));
		}

		// As far as I can tell, there's no field anywhere in vanilla that ties DyeColors to wool items or blocks. So we have to do it ourselves.
		woolBlockItems.put(DyeColor.WHITE, () -> Items.WHITE_WOOL);
		woolBlockItems.put(DyeColor.ORANGE, () -> Items.ORANGE_WOOL);
		woolBlockItems.put(DyeColor.MAGENTA, () -> Items.MAGENTA_WOOL);
		woolBlockItems.put(DyeColor.LIGHT_BLUE, () -> Items.LIGHT_BLUE_WOOL);
		woolBlockItems.put(DyeColor.YELLOW, () -> Items.YELLOW_WOOL);
		woolBlockItems.put(DyeColor.LIME, () -> Items.LIME_WOOL);
		woolBlockItems.put(DyeColor.PINK, () -> Items.PINK_WOOL);
		woolBlockItems.put(DyeColor.GRAY, () -> Items.GRAY_WOOL);
		woolBlockItems.put(DyeColor.LIGHT_GRAY, () -> Items.LIGHT_GRAY_WOOL);
		woolBlockItems.put(DyeColor.CYAN, () -> Items.CYAN_WOOL);
		woolBlockItems.put(DyeColor.PURPLE, () -> Items.PURPLE_WOOL);
		woolBlockItems.put(DyeColor.BLUE, () -> Items.BLUE_WOOL);
		woolBlockItems.put(DyeColor.BROWN, () -> Items.BROWN_WOOL);
		woolBlockItems.put(DyeColor.GREEN, () -> Items.GREEN_WOOL);
		woolBlockItems.put(DyeColor.RED, () -> Items.RED_WOOL);
		woolBlockItems.put(DyeColor.BLACK, () -> Items.BLACK_WOOL);
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> genericBeddingTag = provider.tagW(ContentHelper.ItemTags.BEDDING_MATERIALS);
		for (DyeColor color : DyeColor.values())
		{
			TagKey<Item> colorTag = ContentHelper.ItemTags.BEDDING_MATERIALS_BY_COLOR.get(color);
			provider.tagW(colorTag)
				.add(kapokBlockItems.get(color).get())
				.add(woolBlockItems.get(color).get());
			genericBeddingTag.addTag(colorTag);
		}
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		for (Map.Entry<DyeColor, RegistryObject<Item>> entry : kapokBlockItems.entrySet())
		{
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, entry.getValue().get())
				.requires(entry.getKey().getTag())
				.requires(Ingredient.of(kapokBlockItems
					.values()
					.stream()
					// Exclude dyeing a block the color it already is, e.g., dyeing black kapok black.
					.filter(registryItem -> !registryItem.get().equals(entry.getValue().get()))
					.map(registryItem -> new ItemStack(registryItem.get()))))
//				.unlockedBy("has_needed_dye", provider.hasW(entry.getKey().getTag())) todo
				.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
				.save(output, new ResourceLocation(ModInfo.MODID_LOWER, "dye_" + entry.getValue().getId().getPath()));
		}

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.STRING)
			.pattern("~~~")
			.define('~', kapokTuft.get())
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output, new ResourceLocation(ModInfo.MODID_LOWER, "string"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, kapokBlockItems.get(DyeColor.WHITE).get())
			.pattern("~~")
			.pattern("~~")
			.define('~', kapokTuft.get())
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);
	}

	@Override
	public void finish()
	{
		for (DyeColor color : DyeColor.values())
		{
			// TODO: Any generic uses of wool? Without OreDictionary wildcards, I'm not sure how we'll handle that.
			Modifiers.recipes.convertInput(Ingredient.of(woolBlockItems.get(color).get()), Ingredient.of(ContentHelper.ItemTags.BEDDING_MATERIALS_BY_COLOR.get(color)));
			Modifiers.recipes.excludeOutput(woolBlockItems.get(color).get());
		}
	}

	@Override
	public void datagenBlockStatesAndModels(BlockStateProvider provider)
	{
		kapokBlocks.forEach((color, obj) -> provider.simpleBlock(obj.get(), provider.models().withExistingParent(obj.getId().getPath(), provider.mcLoc(color.getName() + "_wool"))));
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		for (Map.Entry<DyeColor, RegistryObject<Item>> entry : kapokBlockItems.entrySet())
		{
			String color = entry.getKey().getName();
			provider.withExistingParent(color + "_kapok", provider.modLoc(color + "_kapok"));
		}
		provider.basicItem(kapokTuft.get());
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"jungle_leaves_kapok_tuft",
			new SimpleBlockDropLootModifier(
				Blocks.JUNGLE_LEAVES,
				kapokTuft.get(),
				ConstantValue.exactly(0.07f),
				UniformGenerator.between(1, 2)
			)
		);
	}

	@Override
	public BlockLootSubProvider getBlockLootProvider()
	{
		return new GenericBlockLootSubProvider()
		{
			@Override
			protected void generate()
			{
				kapokBlocks.values().forEach(i -> dropSelf(i.get()));
			}

			@Override
			protected Iterable<Block> getKnownBlocks()
			{
				return kapokBlocks.values().stream().map(RegistryObject::get).toList();
			}
		};
	}
}
