package squeek.veganoption.blocks;

import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Necessary because Items.bed is hardcoded in BlockBed getItemDropped method
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

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return state.getValue(PART) == EnumPartType.HEAD ? Items.AIR : bedItem;
	}

	@Override
	public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, Entity player)
	{
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(bedItem);
	}
}
