package squeek.veganoption.content;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.ForgeRegistries;
import squeek.veganoption.ModInfo;

import java.util.Map;
import java.util.Objects;

public class ContentHelper
{
	/** default smelt time in vanilla */
	public static final int DEFAULT_SMELT_TIME = 200;

	/** default campfire cooking time in vanilla */
	public static final int DEFAULT_COOK_TIME = 600;

	/** default smoking time in vanilla */
	public static final int DEFAULT_SMOKE_TIME = 100;

	/**
	 * @param item The item
	 * @param tag The tag
	 * @return true if the given item is in the given tag.
	 */
	public static boolean isItemTaggedAs(Item item, TagKey<Item> tag)
	{
		return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag).contains(item);
	}

	public static class ItemTags
	{
		private ItemTags()
		{
		}

		public static final TagKey<Item> ARMOR_BOOTS = Tags.Items.ARMORS_BOOTS;
		public static final TagKey<Item> ARMOR_LEGGINGS = Tags.Items.ARMORS_LEGGINGS;
		public static final TagKey<Item> ARMOR_CHESTPLATES = Tags.Items.ARMORS_CHESTPLATES;
		public static final TagKey<Item> ARMOR_HELMETS = Tags.Items.ARMORS_HELMETS;
		public static final TagKey<Item> BEDDING_MATERIALS = veganoptionTag("bedding_materials");
		public static final Map<DyeColor, TagKey<Item>> BEDDING_MATERIALS_BY_COLOR;
		public static final TagKey<Item> DUST_WOOD = forgeTag("dusts/wood");
		public static final TagKey<Item> DYES_BLACK = Tags.Items.DYES_BLACK;
		public static final TagKey<Item> DYES_BROWN = Tags.Items.DYES_BROWN;
		public static final TagKey<Item> DYES_GREEN = Tags.Items.DYES_GREEN;
		public static final TagKey<Item> DYES_LIGHT_GRAY = Tags.Items.DYES_LIGHT_GRAY;
		public static final TagKey<Item> DYES_LIME = Tags.Items.DYES_LIME;
		public static final TagKey<Item> EGGS = Tags.Items.EGGS;
		public static final TagKey<Item> EGG_OBJECT = veganoptionTag("eggs/object");
		public static final TagKey<Item> EGG_BAKING = veganoptionTag("eggs/baking");
		public static final TagKey<Item> FEATHERS = Tags.Items.FEATHERS;
		public static final TagKey<Item> FIBRES = forgeTag("fibres");
		public static final TagKey<Item> FLOWERS = net.minecraft.tags.ItemTags.FLOWERS;
		public static final TagKey<Item> FLUFFY_MATERIAL = veganoptionTag("fluffy_material");
		public static final TagKey<Item> FOOD = forgeTag("food");
		public static final TagKey<Item> FOOD_COOKED_FISH = forgeTag("food/cooked_fish");
		public static final TagKey<Item> FOOD_COOKED_MEAT = forgeTag("food/cooked_meat");
		public static final TagKey<Item> FOOD_RAW_FISH = forgeTag("food/raw_fish");
		public static final TagKey<Item> FOOD_RAW_MEAT = forgeTag("food/raw_meat");
		public static final TagKey<Item> GOLD_NUGGETS = Tags.Items.NUGGETS_GOLD;
		public static final TagKey<Item> INK_BLACK = forgeTag("ink/black");
		public static final TagKey<Item> LEATHER = Tags.Items.LEATHER;
		public static final TagKey<Item> LEATHER_BOOTS = veganoptionTag("boots/leather");
		public static final TagKey<Item> LEATHER_LEGGINGS = veganoptionTag("leggings/leather");
		public static final TagKey<Item> LEATHER_CHESTPLATES = veganoptionTag("chestplates/leather");
		public static final TagKey<Item> LEATHER_HELMETS = veganoptionTag("helmets/leather");
		public static final TagKey<Item> LEAVES = net.minecraft.tags.ItemTags.LEAVES;
		public static final TagKey<Item> MILK = forgeTag("milk");
		public static final TagKey<Item> PIGMENT_BLACK = veganoptionTag("pigments/black");
		public static final TagKey<Item> PIGMENT_WHITE = veganoptionTag("pigments/white");
		public static final TagKey<Item> PLANT_MILK_SOURCES = veganoptionTag("plant_milk_sources");
		public static final TagKey<Item> PLASTIC_SHEET = forgeTag("sheets/plastic");
		public static final TagKey<Item> PLASTIC_ROD = forgeTag("rods/plastic");
		public static final TagKey<Item> RAW_SEITAN = veganoptionTag("raw_seitan");
		public static final TagKey<Item> REAGENT_FERMENTED = veganoptionTag("reagents/fermented");
		public static final TagKey<Item> REAGENT_POISONOUS = veganoptionTag("reagents/poisonous");
		public static final TagKey<Item> REAGENT_TEAR = veganoptionTag("reagents/tear");
		public static final TagKey<Item> REAGENT_WATERBREATHING = veganoptionTag("reagents/waterbreathing");
		public static final TagKey<Item> RESIN = veganoptionTag("resin");
		public static final TagKey<Item> RODS = Tags.Items.RODS;
		public static final TagKey<Item> ROSIN = veganoptionTag("rosin");
		public static final TagKey<Item> ROTTEN_MATERIAL = veganoptionTag("rotten_material");
		public static final TagKey<Item> SALTPETER = forgeTag("dusts/saltpeter");
		public static final TagKey<Item> SAPLINGS = net.minecraft.tags.ItemTags.SAPLINGS;
		public static final TagKey<Item> SEEDS = Tags.Items.SEEDS;
		public static final TagKey<Item> SEEDS_SUNFLOWER = forgeTag("seeds/sunflower");
		public static final TagKey<Item> SLIMEBALLS = Tags.Items.SLIMEBALLS;
		public static final TagKey<Item> SOAP = veganoptionTag("soap");
		public static final TagKey<Item> STARCH = veganoptionTag("starch");
		public static final TagKey<Item> STICKS = Tags.Items.RODS_WOODEN;
		public static final TagKey<Item> SULFUR = forgeTag("dusts/sulfur");
		public static final TagKey<Item> VEGETABLE_OIL_SOURCES = veganoptionTag("vegetable_oil_sources");
		public static final TagKey<Item> VEGETABLE_OIL = forgeTag("food/vegetable_oil");
		public static final TagKey<Item> WAX = forgeTag("wax");
		public static final TagKey<Item> WHEAT_FLOUR = forgeTag("wheat_flour");
		public static final TagKey<Item> WHEAT_DOUGH = forgeTag("wheat_dough");
		public static final TagKey<Item> WOOD_ASH = forgeTag("wood_ash");
		public static final TagKey<Item> WOOD_PLANKS = net.minecraft.tags.ItemTags.PLANKS;

		static {
			ImmutableMap.Builder<DyeColor, TagKey<Item>> builder = ImmutableMap.builder();
			for (DyeColor color : DyeColor.values())
			{
				builder.put(color, veganoptionTag("bedding_materials/" + color.getName()));
			}
			BEDDING_MATERIALS_BY_COLOR = builder.build();
		}


		private static TagKey<Item> forgeTag(String name)
		{
			return net.minecraft.tags.ItemTags.create(new ResourceLocation("forge", name));
		}

		private static TagKey<Item> veganoptionTag(String name)
		{
			return net.minecraft.tags.ItemTags.create(new ResourceLocation(ModInfo.MODID_LOWER, name));
		}
	}

	public static class FluidTags
	{
		private FluidTags()
		{
		}

		public static final TagKey<Fluid> MILK = Tags.Fluids.MILK;
		public static final TagKey<Fluid> VEGETABLE_OIL = forgeTag("vegetable_oil");
		public static final TagKey<Fluid> BLACK_INK = forgeTag("black_ink");
		public static final TagKey<Fluid> SOAP_SOLUTION = forgeTag("soap");

		public static TagKey<Fluid> forgeTag(String name)
		{
			return net.minecraft.tags.FluidTags.create(new ResourceLocation("forge", name));
		}
	}
}
