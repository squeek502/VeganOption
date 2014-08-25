package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;

public class BlockEncrustedObsidian extends BlockObsidian
{
	public BlockEncrustedObsidian()
	{
		super();
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return Item.getItemFromBlock(this);
	}
}
