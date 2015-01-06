package squeek.veganoption.content.modules;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockBedGeneric;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.items.ItemBedGeneric;
import cpw.mods.fml.common.registry.GameRegistry;

public class StrawBed implements IContentModule
{
	public static BlockBedGeneric bedStrawBlock;
	public static ItemBedGeneric bedStrawItem;

	@Override
	public void create()
	{
		bedStrawBlock = (BlockBedGeneric) new BlockBedGeneric()
				.setHardness(0.2F)
				.setBlockName(ModInfo.MODID + ".bedStraw")
				.setBlockTextureName(ModInfo.MODID_LOWER + ":straw_bed");
		bedStrawItem = (ItemBedGeneric) new ItemBedGeneric(bedStrawBlock)
				.setMaxStackSize(1)
				.setUnlocalizedName(ModInfo.MODID + ".bedStraw")
				.setTextureName(ModInfo.MODID_LOWER + ":straw_bed");
		bedStrawBlock.setBedItem(bedStrawItem);
		GameRegistry.registerBlock(bedStrawBlock, null, "bedStraw");
		GameRegistry.registerItem(bedStrawItem, "bedStraw");
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bedStrawItem), "~~~", "===", '~', new ItemStack(Blocks.hay_block), '=', ContentHelper.woodPlankOreDict));
	}

	@Override
	public void finish()
	{
	}
}
