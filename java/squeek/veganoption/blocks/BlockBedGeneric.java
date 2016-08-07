package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 *  Necessary because Items.bed is hardcoded in BlockBed getItemDropped method
 */
public class BlockBedGeneric extends BlockBed
{
	public Item bedItem;

	public BlockBedGeneric()
	{
		super();
		this.bedItem = Items.BED;
	}

	public BlockBed setBedItem(Item item)
	{
		bedItem = item;
		return this;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return state.getValue(PART) == EnumPartType.HEAD ? null : bedItem;
	}

	@Override
	public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, Entity player)
	{
		return true;
	}
}
