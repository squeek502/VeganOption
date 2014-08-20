package squeek.veganoption.registry;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.modifications.FernModifier;
import cpw.mods.fml.common.registry.GameRegistry;

public class Content
{
	// jute
	public static BlockRettable juteBundled;
	public static Item juteStalk;
	public static Item juteFibre;

	// burlap
	public static Item burlap;
	public static Item burlapHelmet;
	public static Item burlapChestplate;
	public static Item burlapLeggings;
	public static Item burlapBoots;

	public static void create()
	{
		jute();
		burlap();
		string();
	}

	public static void jute()
	{
		juteFibre = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteFibre")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_fibre");
		GameRegistry.registerItem(juteFibre, "juteFibre");
		OreDictionary.registerOre("fibreBast", new ItemStack(juteFibre));

		juteStalk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteStalk")
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_stalk");
		GameRegistry.registerItem(juteStalk, "juteStalk");

		juteBundled = (BlockRettable) new BlockRettable(juteFibre, 8, 15)
				.setHardness(0.5F)
				.setStepSound(Block.soundTypeGrass)
				.setBlockName(ModInfo.MODID + ".juteBundled")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":jute_block");
		GameRegistry.registerBlock(juteBundled, "juteBundled");
		GameRegistry.addShapedRecipe(new ItemStack(juteBundled), "///", "///", "///", '/', new ItemStack(juteStalk));

		new FernModifier();
	}

	public static void burlap()
	{
		burlap = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".burlap")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":burlap");
		GameRegistry.registerItem(burlap, "burlap");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(burlap), "~~", "~~", '~', "fibreBast"));

		burlapHelmet = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 0)
				.setUnlocalizedName(ModInfo.MODID + ".helmetBurlap")
				.setTextureName("leather_helmet");
		GameRegistry.registerItem(burlapHelmet, "helmetBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapHelmet), "XXX", "X X", 'X', new ItemStack(burlap));

		burlapChestplate = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 1)
				.setUnlocalizedName(ModInfo.MODID + ".chestplateBurlap")
				.setTextureName("leather_chestplate");
		GameRegistry.registerItem(burlapChestplate, "chestplateBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapChestplate), "X X", "XXX", "XXX", 'X', new ItemStack(burlap));

		burlapLeggings = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 2)
				.setUnlocalizedName(ModInfo.MODID + ".leggingsBurlap")
				.setTextureName("leather_leggings");
		GameRegistry.registerItem(burlapLeggings, "leggingsBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapLeggings), "XXX", "X X", "X X", 'X', new ItemStack(burlap));

		burlapBoots = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 3)
				.setUnlocalizedName(ModInfo.MODID + ".bootsBurlap")
				.setTextureName("leather_boots");
		GameRegistry.registerItem(burlapBoots, "bootsBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapBoots), "X X", "X X", 'X', new ItemStack(burlap));
	}

	public static void string()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.string), "~~", '~', "fibreBast"));
	}
}
