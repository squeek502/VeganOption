package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerGrass;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.blocks.BlockJutePlant;
import squeek.veganoption.content.modules.Jute;

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
