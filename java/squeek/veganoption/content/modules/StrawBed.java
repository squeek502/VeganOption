package squeek.veganoption.content.modules;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockBedGeneric;
import squeek.veganoption.blocks.BlockBedStraw;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.items.ItemBedGeneric;
import squeek.veganoption.items.ItemBedStraw;

public class StrawBed implements IContentModule
{
	public static BlockBedGeneric bedStrawBlock;
	public static ItemBedGeneric bedStrawItem;

	@Override
	public void create()
	{
		bedStrawBlock = (BlockBedGeneric) new BlockBedStraw()
				.setHardness(0.2F)
				.setCreativeTab(VeganOption.creativeTab)
				.setUnlocalizedName(ModInfo.MODID + ".bedStraw")
				.setRegistryName(ModInfo.MODID_LOWER, "bedStraw");
		bedStrawItem = (ItemBedGeneric) new ItemBedStraw(bedStrawBlock)
				.setMaxStackSize(1)
				.setCreativeTab(VeganOption.creativeTab)
				.setUnlocalizedName(ModInfo.MODID + ".bedStraw")
				.setRegistryName(ModInfo.MODID_LOWER, "bedStraw");
		bedStrawBlock.setBedItem(bedStrawItem);
		GameRegistry.register(bedStrawBlock);
		GameRegistry.register(bedStrawItem);
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bedStrawItem), "~~~", "===", '~', new ItemStack(Blocks.HAY_BLOCK), '=', ContentHelper.woodPlankOreDict));
	}

	@Override
	public void finish()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSide()
	{
	}
}
