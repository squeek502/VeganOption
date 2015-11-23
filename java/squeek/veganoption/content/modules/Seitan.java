package squeek.veganoption.content.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.items.ItemRawSeitan;
import cpw.mods.fml.common.registry.GameRegistry;

// TODO: In-world dough creation (flour in water = dough)
// TODO: Tooltips, usage, and recipe text
public class Seitan implements IContentModule
{
	public static Item wheatFlour;
	public static Item wheatDough;
	public static Item seitanRaw;
	public static Item seitanCooked;
	public static ItemStack seitanRawStack;
	public static ItemStack seitanUnwashedStack;

	public static final ItemStack wheatCrusher = new ItemStack(Blocks.piston);

	@Override
	public void create()
	{
		wheatFlour = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".wheatFlour")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":wheat_flour");
		GameRegistry.registerItem(wheatFlour, "wheatFlour");

		wheatDough = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".wheatDough")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":wheat_dough");
		GameRegistry.registerItem(wheatDough, "wheatDough");

		seitanRaw = new ItemRawSeitan()
				.setUnlocalizedName(ModInfo.MODID + ".seitanRaw")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":seitan_raw");
		GameRegistry.registerItem(seitanRaw, "seitanRaw");
		seitanUnwashedStack = new ItemStack(seitanRaw);
		seitanRawStack = new ItemStack(seitanRaw, 1, ItemRawSeitan.META_RAW);

		seitanCooked = new ItemFood(8, 0.8f, false)
				.setUnlocalizedName(ModInfo.MODID + ".seitanCooked")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":seitan_cooked");
		GameRegistry.registerItem(seitanCooked, "seitanCooked");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.wheatFlourOreDict, new ItemStack(wheatFlour));
		OreDictionary.registerOre(ContentHelper.wheatDoughOreDict, new ItemStack(wheatDough));
		OreDictionary.registerOre(ContentHelper.rawSeitanOreDict, seitanRawStack.copy());

		// cooked seitan works as a raw/cooked meat substitute, a la HarvestCraft tofu
		for (String oreDict : ContentHelper.harvestCraftRawMeatOreDicts)
			OreDictionary.registerOre(oreDict, new ItemStack(seitanCooked));
		for (String oreDict : ContentHelper.harvestCraftCookedMeatOreDicts)
			OreDictionary.registerOre(oreDict, new ItemStack(seitanCooked));
	}

	@Override
	public void recipes()
	{
		ContentHelper.addOreSmelting(ContentHelper.rawSeitanOreDict, new ItemStack(seitanCooked), 0.35f);

		GameRegistry.addShapelessRecipe(new ItemStack(wheatFlour), wheatCrusher, new ItemStack(Items.wheat));
		Modifiers.crafting.addInputsToKeepForOutput(new ItemStack(wheatFlour), wheatCrusher);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new ItemStack(wheatFlour), Items.wheat));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(wheatDough), new ItemStack(Items.water_bucket), ContentHelper.wheatFlourOreDict));
		GameRegistry.addRecipe(new ShapelessOreRecipe(seitanUnwashedStack.copy(), new ItemStack(Items.water_bucket), ContentHelper.wheatDoughOreDict));
		for (int outputMeta = 1; outputMeta < ItemRawSeitan.META_RAW; outputMeta++)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(seitanRaw, 1, outputMeta), new ItemStack(Items.water_bucket), new ItemStack(seitanRaw, 1, outputMeta - 1)));
		}
		GameRegistry.addRecipe(new ShapelessOreRecipe(seitanRawStack.copy(), new ItemStack(Items.water_bucket), new ItemStack(seitanRaw, 1, ItemRawSeitan.META_RAW - 1)));
	}

	@Override
	public void finish()
	{
	}
}
