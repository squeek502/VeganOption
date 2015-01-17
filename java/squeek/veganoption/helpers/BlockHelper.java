package squeek.veganoption.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHelper
{
	public static final float BLOCK_HARDNESS_UNBREAKABLE = -1.0f;

	public static class BlockPos
	{
		public final World world;
		public final int x, y, z;

		public BlockPos(World world, int x, int y, int z)
		{
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Block getBlock()
		{
			return world.getBlock(x, y, z);
		}

		public int getMeta()
		{
			return world.getBlockMetadata(x, y, z);
		}

		public TileEntity getTile()
		{
			return world.getTileEntity(x, y, z);
		}

		public BlockPos getOffset(int x, int y, int z)
		{
			return new BlockPos(this.world, this.x + x, this.y + y, this.z + z);
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((world == null) ? 0 : world.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BlockPos other = (BlockPos) obj;
			if (world == null)
			{
				if (other.world != null)
					return false;
			}
			else if (!world.equals(other.world))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}
	}

	public static BlockPos blockPos(World world, int x, int y, int z)
	{
		return new BlockPos(world, x, y, z);
	}

	public static boolean isMaterial(BlockPos blockPos, Material material)
	{
		return blockPos.getBlock().getMaterial() == material;
	}

	public static boolean isAdjacentToMaterial(BlockPos blockPos, Material material)
	{
		for (BlockPos blockToCheck : getBlocksAdjacentTo(blockPos))
		{
			if (isMaterial(blockToCheck, material))
				return true;
		}
		return false;
	}

	public static BlockPos[] getBlocksAdjacentTo(BlockPos blockPos)
	{
		return new BlockPos[]{
		blockPos.getOffset(-1, 0, 0), blockPos.getOffset(1, 0, 0),
		blockPos.getOffset(0, 0, -1), blockPos.getOffset(0, 0, 1)
		};
	}

	public static boolean isWater(BlockPos blockPos)
	{
		return isMaterial(blockPos, Material.water);
	}

	public static boolean isAdjacentToOrCoveredInWater(BlockPos blockPos)
	{
		return isWater(blockPos.getOffset(0, 1, 0)) || isAdjacentToWater(blockPos);
	}

	public static boolean isAdjacentToWater(BlockPos blockPos)
	{
		return isAdjacentToMaterial(blockPos, Material.water);
	}

	public static BlockPos followWaterStreamToSourceBlock(BlockPos blockPos)
	{
		return followWaterStreamToSourceBlock(blockPos, new HashSet<BlockPos>());
	}

	public static BlockPos followWaterStreamToSourceBlock(BlockPos blockPos, Set<BlockPos> blocksChecked)
	{
		if (blockPos.getMeta() == 0)
			return blockPos;

		List<BlockPos> blocksToCheck = new ArrayList<BlockPos>();
		blocksToCheck.add(blockPos.getOffset(0, 1, 0));
		blocksToCheck.addAll(Arrays.asList(getBlocksAdjacentTo(blockPos)));

		for (BlockPos blockToCheck : blocksToCheck)
		{
			if (isWater(blockToCheck) && !blocksChecked.contains(blockToCheck))
			{
				if (blockToCheck.getMeta() == 0)
					return blockToCheck;
				else
				{
					blocksChecked.add(blockToCheck);
					BlockPos foundSourceBlock = followWaterStreamToSourceBlock(blockToCheck, blocksChecked);

					if (foundSourceBlock != null)
						return foundSourceBlock;
				}
			}
		}
		return null;
	}

	public static BlockPos[] getBlocksInRadiusAround(BlockPos centerBlock, int radius)
	{
		List<BlockPos> blocks = new ArrayList<BlockPos>();
		int radiusSq = radius * radius;
		for (int xOffset = 0; xOffset <= radius; xOffset++)
		{
			for (int yOffset = 0; yOffset <= radius; yOffset++)
			{
				for (int zOffset = 0; zOffset <= radius; zOffset++)
				{
					BlockPos block = centerBlock.getOffset(xOffset, yOffset, zOffset);
					int xDelta = block.x - centerBlock.x;
					int yDelta = block.y - centerBlock.y;
					int zDelta = block.z - centerBlock.z;
					int deltaLengthSq = xDelta * xDelta + yDelta * yDelta + zDelta * zDelta;
					if (deltaLengthSq <= radiusSq)
					{
						blocks.add(block);
						blocks.add(centerBlock.getOffset(-xOffset, yOffset, zOffset));
						blocks.add(centerBlock.getOffset(xOffset, yOffset, -zOffset));
						blocks.add(centerBlock.getOffset(-xOffset, yOffset, -zOffset));
						blocks.add(centerBlock.getOffset(xOffset, -yOffset, zOffset));
						blocks.add(centerBlock.getOffset(xOffset, -yOffset, -zOffset));
						blocks.add(centerBlock.getOffset(-xOffset, -yOffset, zOffset));
						blocks.add(centerBlock.getOffset(-xOffset, -yOffset, -zOffset));
					}
				}
			}
		}
		return blocks.toArray(new BlockPos[0]);
	}

	public static BlockPos[] filterBlockListToBreakableBlocks(BlockPos... blocks)
	{
		List<BlockPos> filteredBlocks = new ArrayList<BlockPos>();
		for (BlockPos blockPos : blocks)
		{
			Block block = blockPos.getBlock();

			if (block == null)
				continue;

			if (block.isAir(blockPos.world, blockPos.x, blockPos.y, blockPos.z))
				continue;

			if (isBlockUnbreakable(block, blockPos.world, blockPos.x, blockPos.y, blockPos.z))
				continue;

			filteredBlocks.add(blockPos);
		}
		return filteredBlocks.toArray(new BlockPos[0]);
	}

	public static boolean isBlockUnbreakable(Block block, World world, int x, int y, int z)
	{
		return block.getBlockHardness(world, x, y, z) == BLOCK_HARDNESS_UNBREAKABLE;
	}
}
