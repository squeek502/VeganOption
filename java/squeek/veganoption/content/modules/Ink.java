package squeek.veganoption.content.modules;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import cpw.mods.fml.common.registry.GameRegistry;

public class Ink implements IContentModule
{
	public static Item blackVegetableOilInk;
	public static Item whiteVegetableOilInk;
	public static Item waxVegetable;

	@Override
	public void create()
	{
		waxVegetable = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".waxVegetable")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_wax");
		GameRegistry.registerItem(waxVegetable, "waxVegetable");

		blackVegetableOilInk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOilBlack")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_oil_ink_black")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(blackVegetableOilInk, "inkVegetableOilBlack");

		whiteVegetableOilInk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOilWhite")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_oil_ink_white")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(whiteVegetableOilInk, "inkVegetableOilWhite");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.blackPigmentOreDict, ContentHelper.charcoal.copy());
		OreDictionary.registerOre(ContentHelper.whitePigmentOreDict, Items.quartz);
		OreDictionary.registerOre(ContentHelper.blackDyeOreDict, blackVegetableOilInk);
		OreDictionary.registerOre(ContentHelper.whiteDyeOreDict, whiteVegetableOilInk);
		OreDictionary.registerOre(ContentHelper.waxOreDict, new ItemStack(waxVegetable));
		OreDictionary.registerOre(ContentHelper.waxOreDictForestry, new ItemStack(waxVegetable));
		OreDictionary.registerOre(ContentHelper.waxOreDictHarvestCraft, new ItemStack(waxVegetable));
	}

	@Override
	public void recipes()
	{
		ContentHelper.addOreSmelting(ContentHelper.vegetableOilOreDict, new ItemStack(waxVegetable), 0.2f);

		GameRegistry.addRecipe(new ShapelessOreRecipe(blackVegetableOilInk, ContentHelper.vegetableOilOreDict, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.blackPigmentOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(blackVegetableOilInk), ContentHelper.vegetableOilOreDict);

		GameRegistry.addRecipe(new ShapelessOreRecipe(whiteVegetableOilInk, ContentHelper.vegetableOilOreDict, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.whitePigmentOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(whiteVegetableOilInk), ContentHelper.vegetableOilOreDict);
	}

	@Override
	public void finish()
	{
	}

}
