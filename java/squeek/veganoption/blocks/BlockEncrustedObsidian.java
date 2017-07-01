package squeek.veganoption.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.BlockHelper;

import java.util.Random;

public class BlockEncrustedObsidian extends BlockObsidian
{
	public BlockEncrustedObsidian()
	{
		super();
		setSoundType(SoundType.STONE);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	public static void tryPlacePortalAdjacentTo(World world, BlockPos pos)
	{
		for (BlockPos blockPosToCheck : BlockHelper.getBlocksAdjacentTo(pos))
		{
			Block blockToCheck = world.getBlockState(blockPosToCheck).getBlock();
			if (blockToCheck != Ender.enderRift && BlockEnderRift.isValidPortalLocation(world, blockPosToCheck) && blockToCheck.isReplaceable(world, blockPosToCheck))
			{
				world.setBlockState(blockPosToCheck, Ender.enderRift.getDefaultState());
			}
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, world, pos, blockIn, fromPos);

		tryPlacePortalAdjacentTo(world, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		tryPlacePortalAdjacentTo(world, pos);
	}
}
