package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import squeek.veganoption.blocks.BlockJutePlant;
import squeek.veganoption.content.modules.Jute;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockJutePlant extends ItemBlock
{
	public ItemBlockJutePlant(Block block)
	{
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta)
	{
		return Jute.jutePlant.getIcon(0, BlockJutePlant.BOTTOM_META_GROWTH_MAX);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int pass)
	{
		return ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}
}
