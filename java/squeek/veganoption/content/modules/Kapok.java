package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import squeek.veganoption.helpers.ConstantHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class Kapok implements IContentModule
{
	public static Item kapokTuft;
	public static BlockColored kapokBlock;

	@Override
	public void create()
	{
		kapokTuft = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".kapokTuft")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":kapok_tuft");
		GameRegistry.registerItem(kapokTuft, "kapokTuft");

		kapokBlock = (BlockColored) new BlockColored(Material.cloth)
				.setHardness(0.8F)
				.setStepSound(Block.soundTypeCloth)
				.setBlockName(ModInfo.MODID + ".kapok")
				.setBlockTextureName("wool_colored");
		GameRegistry.registerBlock(kapokBlock, ItemCloth.class, "kapok");

	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.kapokOreDict, new ItemStack(kapokTuft));
		OreDictionary.registerOre(ContentHelper.woolOreDict, new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre(ContentHelper.woolOreDict, new ItemStack(kapokBlock, 1, OreDictionary.WILDCARD_VALUE));
		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			OreDictionary.registerOre(ContentHelper.woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(Blocks.wool, 1, 15 - i));
			OreDictionary.registerOre(ContentHelper.woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock, 1, 15 - i));
		}
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), ContentHelper.woolOreDict);
		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			Modifiers.recipes.convertInput(new ItemStack(Blocks.wool, 1, 15 - i), ContentHelper.woolOreDict + ConstantHelper.dyeColors[i]);
			Modifiers.recipes.excludeOutput(new ItemStack(Blocks.wool, 1, 15 - i));
		}

		GameRegistry.addShapedRecipe(new ItemStack(kapokBlock), "~~", "~~", '~', new ItemStack(kapokTuft));

		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(kapokBlock, 1, 15 - i), "dye" + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock)));
		}

		BlockSpecifier jungleLeavesSpecifier = new BlockSpecifier(Blocks.leaves, 3)
		{
			@Override
			public boolean metaMatches(int meta)
			{
				return this.meta == (meta & 3);
			}
		};
		Modifiers.drops.addDropsToBlock(jungleLeavesSpecifier, new DropSpecifier(new ItemStack(kapokTuft), 0.07f, 1, 2));

		GameRegistry.addShapedRecipe(new ItemStack(Items.string), "~~~", '~', kapokTuft);
	}

	@Override
	public void finish()
	{
	}
}
