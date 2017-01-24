package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerGrass;

public class ItemBlockJutePlant extends ItemBlock
{
	public ItemBlockJutePlant(Block block)
	{
		super(block);
	}

	public static class ColorHandler implements IItemColor
	{
		public int getColorFromItemstack(ItemStack itemStack, int tintIndex)
		{
			return ColorizerGrass.getGrassColor(0.5D, 1D);
		}
	}
}
