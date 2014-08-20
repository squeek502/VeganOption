package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.BlockBed;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 *  Necessary because Items.bed is hardcoded in BlockBed getItemDropped method
 */
public class BlockBedGeneric extends BlockBed
{
	Item bedItem;

	public BlockBedGeneric()
	{
		super();
		this.bedItem = Items.bed;
	}

	public BlockBed setBedItem(Item item)
	{
		bedItem = item;
		return this;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return isBlockHeadOfBed(p_149650_1_) ? Item.getItemById(0) : bedItem;
	}
}
