package squeek.veganoption.content.modules;

import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockKapok;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import squeek.veganoption.helpers.ConstantHelper;

import javax.annotation.Nonnull;

public class Kapok implements IContentModule
{
	public static Item kapokTuft;
	public static BlockColored kapokBlock;

	@Override
	public void create()
	{
		kapokTuft = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".kapokTuft")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "kapokTuft");
		GameRegistry.register(kapokTuft);

		kapokBlock = (BlockColored) new BlockKapok(Material.CLOTH)
				.setHardness(0.8F)
				.setCreativeTab(VeganOption.creativeTab)
				.setUnlocalizedName(ModInfo.MODID + ".kapok")
				.setRegistryName(ModInfo.MODID_LOWER, "kapok");
		GameRegistry.register(kapokBlock);
		GameRegistry.register(new ItemCloth(kapokBlock).setRegistryName(kapokBlock.getRegistryName()));
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.kapokOreDict, new ItemStack(kapokTuft));
		OreDictionary.registerOre(ContentHelper.vegetableOilSourceOreDict, new ItemStack(kapokTuft));
		OreDictionary.registerOre(ContentHelper.woolOreDict, new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre(ContentHelper.woolOreDict, new ItemStack(kapokBlock, 1, OreDictionary.WILDCARD_VALUE));
		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			OreDictionary.registerOre(ContentHelper.woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(Blocks.WOOL, 1, 15 - i));
			OreDictionary.registerOre(ContentHelper.woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock, 1, 15 - i));
		}
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), ContentHelper.woolOreDict);
		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			Modifiers.recipes.convertInput(new ItemStack(Blocks.WOOL, 1, 15 - i), ContentHelper.woolOreDict + ConstantHelper.dyeColors[i]);
			Modifiers.recipes.excludeOutput(new ItemStack(Blocks.WOOL, 1, 15 - i));
		}

		GameRegistry.addShapedRecipe(new ItemStack(kapokBlock), "~~", "~~", '~', new ItemStack(kapokTuft));

		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(kapokBlock, 1, 15 - i), "dye" + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock)));
		}

		BlockSpecifier jungleLeavesSpecifier = new BlockSpecifier(Blocks.LEAVES, BlockPlanks.EnumType.JUNGLE.getMetadata())
		{
			@Override
			public boolean stateMatches(IBlockState state)
			{
				return state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.JUNGLE);
			}
		};
		Modifiers.drops.addDropsToBlock(jungleLeavesSpecifier, new DropSpecifier(new ItemStack(kapokTuft), 0.07f, 1, 2));

		GameRegistry.addShapedRecipe(new ItemStack(Items.STRING), "~~~", '~', kapokTuft);
	}

	@Override
	public void finish()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePost()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(kapokTuft);
		Item kapokBlockItem = Item.getItemFromBlock(kapokBlock);
		if (kapokBlockItem != null)
		{
			for (EnumDyeColor color : EnumDyeColor.values())
			{
				ModelLoader.setCustomModelResourceLocation(kapokBlockItem, color.getMetadata(), new ModelResourceLocation(kapokBlock.getRegistryName(), "color=" + color.getName()));
			}
		}
	}
}
