package squeek.veganoption.content.modules;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import cpw.mods.fml.common.registry.GameRegistry;

public class Burlap implements IContentModule
{
	public static Item burlap;
	public static ItemArmor burlapHelmet;
	public static ItemArmor burlapChestplate;
	public static ItemArmor burlapLeggings;
	public static ItemArmor burlapBoots;

	@Override
	public void create()
	{
		burlap = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".burlap")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":burlap");
		GameRegistry.registerItem(burlap, "burlap");

		burlapHelmet = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 0)
				.setUnlocalizedName(ModInfo.MODID + ".helmetBurlap")
				.setTextureName("leather_helmet");
		GameRegistry.registerItem(burlapHelmet, "helmetBurlap");

		burlapChestplate = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 1)
				.setUnlocalizedName(ModInfo.MODID + ".chestplateBurlap")
				.setTextureName("leather_chestplate");
		GameRegistry.registerItem(burlapChestplate, "chestplateBurlap");

		burlapLeggings = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 2)
				.setUnlocalizedName(ModInfo.MODID + ".leggingsBurlap")
				.setTextureName("leather_leggings");
		GameRegistry.registerItem(burlapLeggings, "leggingsBurlap");

		burlapBoots = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 3)
				.setUnlocalizedName(ModInfo.MODID + ".bootsBurlap")
				.setTextureName("leather_boots");
		GameRegistry.registerItem(burlapBoots, "bootsBurlap");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.leatherOreDict, new ItemStack(Items.leather));
		OreDictionary.registerOre(ContentHelper.leatherOreDict, new ItemStack(burlap));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.excludeOutput(new ItemStack(Items.leather_helmet));
		Modifiers.recipes.excludeOutput(new ItemStack(Items.leather_chestplate));
		Modifiers.recipes.excludeOutput(new ItemStack(Items.leather_leggings));
		Modifiers.recipes.excludeOutput(new ItemStack(Items.leather_boots));
		Modifiers.recipes.convertInput(new ItemStack(Items.leather), ContentHelper.leatherOreDict);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(burlap), "~~", "~~", '~', ContentHelper.bastFibreOreDict));

		GameRegistry.addRecipe(new ItemStack(burlapHelmet), "XXX", "X X", 'X', new ItemStack(burlap));
		GameRegistry.addRecipe(new ItemStack(burlapChestplate), "X X", "XXX", "XXX", 'X', new ItemStack(burlap));
		GameRegistry.addRecipe(new ItemStack(burlapLeggings), "XXX", "X X", "X X", 'X', new ItemStack(burlap));
		GameRegistry.addRecipe(new ItemStack(burlapBoots), "X X", "X X", 'X', new ItemStack(burlap));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.string), "~~", '~', ContentHelper.bastFibreOreDict));
	}

	@Override
	public void finish()
	{
	}
}
