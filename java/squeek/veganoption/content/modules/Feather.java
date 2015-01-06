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

// currently depends on Kapok
public class Feather implements IContentModule
{
	public static Item fauxFeather;

	@Override
	public void create()
	{
		fauxFeather = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".fauxFeather")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName("feather");
		GameRegistry.registerItem(fauxFeather, "fauxFeather");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.featherOreDict, new ItemStack(Items.feather));
		OreDictionary.registerOre(ContentHelper.featherOreDict, new ItemStack(fauxFeather));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Items.feather), ContentHelper.featherOreDict);

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fauxFeather), ContentHelper.kapokOreDict, ContentHelper.plasticRodOreDict));
	}

	@Override
	public void finish()
	{
	}

}
