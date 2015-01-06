package squeek.veganoption.content.modules;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import cpw.mods.fml.common.registry.GameRegistry;

public class Ink implements IContentModule
{
	public static Item inkVegetableOil;
	public static Item waxVegetable;

	@Override
	public void create()
	{
		waxVegetable = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".waxVegetable")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_wax");
		GameRegistry.registerItem(waxVegetable, "waxVegetable");

		inkVegetableOil = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOil")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_oil_ink")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(inkVegetableOil, "inkVegetableOil");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.blackPigmentOreDict, ContentHelper.charcoal.copy());
		OreDictionary.registerOre(ContentHelper.inkSacOreDict, inkVegetableOil);
		OreDictionary.registerOre(ContentHelper.waxOreDict, new ItemStack(waxVegetable));
		OreDictionary.registerOre(ContentHelper.waxOreDictForestry, new ItemStack(waxVegetable));
	}

	@Override
	public void recipes()
	{
		ContentHelper.addOreSmelting(ContentHelper.vegetableOilOreDict, new ItemStack(waxVegetable), 0.2f);

		GameRegistry.addRecipe(new ShapelessOreRecipe(inkVegetableOil, ContentHelper.vegetableOilOreDict, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.blackPigmentOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(inkVegetableOil), ContentHelper.vegetableOilOreDict);
	}

	@Override
	public void finish()
	{
	}

}
