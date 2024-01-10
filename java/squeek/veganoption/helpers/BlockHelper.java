package squeek.veganoption.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.*;
import java.util.function.Predicate;

public class BlockHelper
{
	public static final float BLOCK_HARDNESS_UNBREAKABLE = Blocks.BEDROCK.defaultDestroyTime();

	public static BlockPos[] getBlocksAdjacentTo(BlockPos blockPos)
	{
		return new BlockPos[]{
			blockPos.relative(Direction.NORTH), blockPos.relative(Direction.SOUTH),
			blockPos.relative(Direction.EAST), blockPos.relative(Direction.WEST)
		};
	}

	public static boolean isWater(Level level, BlockPos blockPos)
	{
		return level.getBlockState(blockPos).getFluidState().is(FluidTags.WATER);
	}

	public static boolean isAdjacentToOrCoveredInWater(Level world, BlockPos blockPos)
	{
		return isWater(world, blockPos.above()) || isAdjacentToWater(world, blockPos);
	}

	public static boolean isAdjacentToWater(Level level, BlockPos blockPos)
	{
		for (BlockPos adjacent : getBlocksAdjacentTo(blockPos))
		{
			if (isWater(level, adjacent))
				return true;
		}
		return false;
	}

	public static BlockPos followWaterStreamToSourceBlock(Level level, BlockPos blockPos)
	{
		return followFluidStreamToSourceBlock(level, blockPos, Fluids.WATER);
	}

	public static BlockPos followFluidStreamToSourceBlock(Level level, BlockPos blockPos, Fluid fluid)
	{
		return followFluidStreamToSourceBlock(level, blockPos, fluid, new HashSet<>());
	}

	public static BlockPos followFluidStreamToSourceBlock(Level level, BlockPos blockPos, Fluid fluid, Set<BlockPos> blocksChecked)
	{
		FluidState originFluidState = level.getFluidState(blockPos);
		if (originFluidState.isSourceOfType(fluid))
			return blockPos;

		List<BlockPos> blocksToCheck = new ArrayList<>();
		blocksToCheck.add(blockPos.above());
		blocksToCheck.addAll(Arrays.asList(getBlocksAdjacentTo(blockPos)));

		for (BlockPos blockToCheck : blocksToCheck)
		{
			FluidState fluidStateToCheck = level.getFluidState(blockToCheck);
			if (fluidStateToCheck.getFluidType() == fluid.getFluidType() && !blocksChecked.contains(blockToCheck))
			{
				if (fluidStateToCheck.isSource())
					return blockToCheck;
				else
				{
					blocksChecked.add(blockToCheck);
					BlockPos foundSourceBlock = followFluidStreamToSourceBlock(level, blockToCheck, fluid, blocksChecked);

					if (foundSourceBlock != null)
						return foundSourceBlock;
				}
			}
		}
		return null;
	}

	public static BlockPos[] getBlocksInRadiusAround(BlockPos centerBlock, int radius)
	{
		Set<BlockPos> blocks = new HashSet<>();
		int radiusSq = radius * radius;
		for (int xOffset = 0; xOffset <= radius; xOffset++)
		{
			for (int yOffset = 0; yOffset <= radius; yOffset++)
			{
				for (int zOffset = 0; zOffset <= radius; zOffset++)
				{
					BlockPos block = centerBlock.offset(xOffset, yOffset, zOffset);
					int xDelta = block.getX() - centerBlock.getX();
					int yDelta = block.getY() - centerBlock.getY();
					int zDelta = block.getZ() - centerBlock.getZ();
					int deltaLengthSq = xDelta * xDelta + yDelta * yDelta + zDelta * zDelta;
					if (deltaLengthSq <= radiusSq)
					{
						blocks.add(block);
						blocks.add(centerBlock.offset(-xOffset, yOffset, zOffset));
						blocks.add(centerBlock.offset(xOffset, yOffset, -zOffset));
						blocks.add(centerBlock.offset(-xOffset, yOffset, -zOffset));
						blocks.add(centerBlock.offset(xOffset, -yOffset, zOffset));
						blocks.add(centerBlock.offset(xOffset, -yOffset, -zOffset));
						blocks.add(centerBlock.offset(-xOffset, -yOffset, zOffset));
						blocks.add(centerBlock.offset(-xOffset, -yOffset, -zOffset));
					}
				}
			}
		}
		return blocks.toArray(new BlockPos[0]);
	}

	/**
	 * Filters the provided array of BlockPos to those which match the predicate
	 */
	public static BlockPos[] filterBlockList(Predicate<BlockState> predicate, Level level, BlockPos... blocks)
	{
		List<BlockPos> filteredBlocks = new ArrayList<>();
		for (BlockPos blockPos : blocks)
		{
			BlockState state = level.getBlockState(blockPos);
			if (predicate.test(state))
				filteredBlocks.add(blockPos);
		}
		return filteredBlocks.toArray(new BlockPos[0]);
	}

	/**
	 * Filters the provided array of BlockPos to those which can be broken, i.e., are not air, liquid, or tagged as unbreakable (bedrock)
	 */
	public static BlockPos[] filterBlockListToBreakable(Level level, BlockPos... blocks)
	{
		return filterBlockList((state) -> !state.isAir() && state.getFluidState().isEmpty() && !isBlockUnbreakable(state), level, blocks);
	}

	public static boolean isBlockUnbreakable(BlockState state)
	{
		return state.getBlock().defaultDestroyTime() == BLOCK_HARDNESS_UNBREAKABLE;
	}

	public static void setBlockToAir(Level level, BlockPos pos)
	{
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}

	/**
	 * Checks if all the blocks below are logs, and stops at dirt/farmland, then checks if all the blocks above are logs, and stops at leaves.
	 * Checks the PERSISTENT state value to determine this is indeed a true tree.
	 * <br/>
	 * This assumes that the blockstate at the startingPos has already been confirmed to be a valid log (not leaves).
	 */
	public static boolean isValidTree(Level level, BlockPos startingPos, Block log, Block leaves)
	{
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(startingPos.getX(), startingPos.getY(), startingPos.getZ());
		for (int y = startingPos.getY(); y > level.getMinBuildHeight(); y--)
		{
			mutablePos.setY(y);
			BlockState state = level.getBlockState(mutablePos);
			if (!state.is(log))
			{
				if (!state.is(BlockTags.DIRT) && !state.is(Blocks.FARMLAND))
					return false;
				else break;
			}
		}
		for (int y = startingPos.getY(); y < level.getMaxBuildHeight(); y++)
		{
			mutablePos.setY(y);
			BlockState state = level.getBlockState(mutablePos);
			if (!state.is(log))
			{
				if (!state.is(leaves) || (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)))
					return false;
				else break;
			}
		}
		return true;
	}
}
