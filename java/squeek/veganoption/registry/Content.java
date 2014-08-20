package squeek.veganoption.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.helpers.ConstantHelper;
import squeek.veganoption.modifications.FernModifier;
import squeek.veganoption.modifications.JungleLeavesModifier;
import squeek.veganoption.modifications.RecipeModifier;
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

	// kapok
	public static Item kapokTuft;
	public static Block kapokBlock;
	public static Item kapokItem;

	// faux feather
	public static Item fauxFeather;

	// oredict
	public static final String leatherOreDict = "materialLeather";
	public static final String woolOreDict = "materialBedding";
	public static final String featherOreDict = "materialFeather";

	public static RecipeModifier recipeModifier = new RecipeModifier();

	public static void create()
	{
		jute();
		burlap();
		kapok();
		fauxFeather();
		palliasse();
		string();
	}

	public static void finish()
	{
		recipeModifier.replaceRecipes();
	}

	private static void jute()
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

	private static void burlap()
	{
		OreDictionary.registerOre(leatherOreDict, new ItemStack(Items.leather));
		recipeModifier.convertInput(new ItemStack(Items.leather), leatherOreDict);

		burlap = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".burlap")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":burlap");
		GameRegistry.registerItem(burlap, "burlap");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(burlap), "~~", "~~", '~', "fibreBast"));
		OreDictionary.registerOre(leatherOreDict, new ItemStack(burlap));

		burlapHelmet = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 0)
				.setUnlocalizedName(ModInfo.MODID + ".helmetBurlap")
				.setTextureName("leather_helmet");
		GameRegistry.registerItem(burlapHelmet, "helmetBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapHelmet), "XXX", "X X", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_helmet));

		burlapChestplate = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 1)
				.setUnlocalizedName(ModInfo.MODID + ".chestplateBurlap")
				.setTextureName("leather_chestplate");
		GameRegistry.registerItem(burlapChestplate, "chestplateBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapChestplate), "X X", "XXX", "XXX", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_chestplate));

		burlapLeggings = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 2)
				.setUnlocalizedName(ModInfo.MODID + ".leggingsBurlap")
				.setTextureName("leather_leggings");
		GameRegistry.registerItem(burlapLeggings, "leggingsBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapLeggings), "XXX", "X X", "X X", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_leggings));

		burlapBoots = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 3)
				.setUnlocalizedName(ModInfo.MODID + ".bootsBurlap")
				.setTextureName("leather_boots");
		GameRegistry.registerItem(burlapBoots, "bootsBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapBoots), "X X", "X X", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_boots));
	}

	private static void string()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.string), "~~", '~', "fibreBast"));
		GameRegistry.addShapedRecipe(new ItemStack(Items.string), "~~~", '~', kapokTuft);
	}

	private static void kapok()
	{
		OreDictionary.registerOre(woolOreDict, new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));
		recipeModifier.convertInput(new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), woolOreDict);
		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			OreDictionary.registerOre(woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(Blocks.wool, 1, 15 - i));
			recipeModifier.convertInput(new ItemStack(Blocks.wool, 1, 15 - i), woolOreDict + ConstantHelper.dyeColors[i]);
			recipeModifier.excludeOutput(new ItemStack(Blocks.wool, 1, 15 - i));
		}

		kapokTuft = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".kapokTuft")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":kapok_tuft");
		GameRegistry.registerItem(kapokTuft, "kapokTuft");

		kapokBlock = new BlockColored(Material.cloth)
				.setHardness(0.8F)
				.setStepSound(Block.soundTypeCloth)
				.setBlockName(ModInfo.MODID + ".kapok")
				.setBlockTextureName("wool_colored");
		GameRegistry.registerBlock(kapokBlock, ItemCloth.class, "kapok");
		OreDictionary.registerOre(woolOreDict, new ItemStack(kapokBlock, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addShapedRecipe(new ItemStack(kapokBlock), "~~", "~~", '~', new ItemStack(kapokTuft));

		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			OreDictionary.registerOre(woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock, 1, 15 - i));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(kapokBlock, 1, 15 - i), "dye" + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock)));
		}

		new JungleLeavesModifier();
	}

	private static void fauxFeather()
	{
		OreDictionary.registerOre(featherOreDict, new ItemStack(Items.feather));
		recipeModifier.convertInput(new ItemStack(Items.feather), featherOreDict);

		fauxFeather = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".fauxFeather")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName("feather");
		GameRegistry.registerItem(fauxFeather, "fauxFeather");
		OreDictionary.registerOre(featherOreDict, new ItemStack(fauxFeather));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fauxFeather), new ItemStack(kapokTuft), "stickWood"));
	}

	private static void palliasse()
	{
		// TODO: Add a separate straw bed
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.bed), "~~~", "===", '~', new ItemStack(Blocks.hay_block), '=', "plankWood"));
	}
}
