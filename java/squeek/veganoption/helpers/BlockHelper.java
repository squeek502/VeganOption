package squeek.veganoption.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.*;

public class BlockHelper
{
	public static final float BLOCK_HARDNESS_UNBREAKABLE = -1.0f;

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

	public static BlockPos[] filterBlockListToBreakableBlocks(Level level, BlockPos... blocks)
	{
		List<BlockPos> filteredBlocks = new ArrayList<>();
		for (BlockPos blockPos : blocks)
		{
			BlockState state = level.getBlockState(blockPos);
			Block block = state.getBlock();

			if (state.isAir())
				continue;

			if (isBlockUnbreakable(level, blockPos))
				continue;

			if (!state.getFluidState().isEmpty())
				continue;

			filteredBlocks.add(blockPos);
		}
		return filteredBlocks.toArray(new BlockPos[0]);
	}

	public static boolean isBlockUnbreakable(Level level, BlockPos pos)
	{
		return level.getBlockState(pos).getBlock().defaultDestroyTime() == BLOCK_HARDNESS_UNBREAKABLE;
	}

	public static void setBlockToAir(Level level, BlockPos pos)
	{
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}
}
