package squeek.veganoption.content.modules;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import cpw.mods.fml.common.registry.GameRegistry;

public class Bioplastic implements IContentModule
{
	public static Item bioplastic;
	public static Item plasticRod;

	@Override
	public void create()
	{
		bioplastic = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".bioplastic")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":bioplastic");
		GameRegistry.registerItem(bioplastic, "bioplastic");

		plasticRod = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".plasticRod")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":plastic_rod");
		GameRegistry.registerItem(plasticRod, "plasticRod");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.plasticOreDict, bioplastic);
		OreDictionary.registerOre(ContentHelper.plasticRodOreDict, plasticRod);
	}

	@Override
	public void recipes()
	{
		ContentHelper.addOreSmelting(ContentHelper.starchOreDict, new ItemStack(bioplastic, 2), 0.35f);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(plasticRod, 4), "p", "p", 'p', ContentHelper.plasticOreDict));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.blaze_rod), ContentHelper.plasticRodOreDict, ContentHelper.rosinOreDict, ContentHelper.waxOreDict, new ItemStack(Items.flint_and_steel, 1, OreDictionary.WILDCARD_VALUE)));
		Modifiers.crafting.addInputsToKeepForOutput(new ItemStack(Items.blaze_rod), new ItemStack(Items.flint_and_steel, 1, OreDictionary.WILDCARD_VALUE));
	}

	@Override
	public void finish()
	{
	}

}
