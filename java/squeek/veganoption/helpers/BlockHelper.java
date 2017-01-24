package squeek.veganoption.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BlockHelper
{
	public static final float BLOCK_HARDNESS_UNBREAKABLE = -1.0f;

	private static final Method createStackedBlock = ReflectionHelper.findMethod(Block.class, null, new String[] {"createStackedBlock", "func_180643_i"}, IBlockState.class);
	public static ItemStack blockStateToItemStack(IBlockState state)
	{
		try
		{
			return (ItemStack) createStackedBlock.invoke(state.getBlock(), state);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static boolean isMaterial(World world, BlockPos blockPos, Material material)
	{
		return world.getBlockState(blockPos).getMaterial() == material;
	}

	public static boolean isAdjacentToMaterial(World world, BlockPos blockPos, Material material)
	{
		for (BlockPos blockToCheck : getBlocksAdjacentTo(blockPos))
		{
			if (isMaterial(world, blockToCheck, material))
				return true;
		}
		return false;
	}

	public static BlockPos[] getBlocksAdjacentTo(BlockPos blockPos)
	{
		return new BlockPos[]{
		blockPos.offset(EnumFacing.NORTH), blockPos.offset(EnumFacing.SOUTH),
		blockPos.offset(EnumFacing.EAST), blockPos.offset(EnumFacing.WEST)
		};
	}

	public static boolean isWater(World world, BlockPos blockPos)
	{
		return isMaterial(world, blockPos, Material.WATER);
	}

	public static boolean isAdjacentToOrCoveredInWater(World world, BlockPos blockPos)
	{
		return isWater(world, blockPos.up()) || isAdjacentToWater(world, blockPos);
	}

	public static boolean isAdjacentToWater(World world, BlockPos blockPos)
	{
		return isAdjacentToMaterial(world, blockPos, Material.WATER);
	}

	public static BlockPos followWaterStreamToSourceBlock(World world, BlockPos blockPos)
	{
		return followFluidStreamToSourceBlock(world, blockPos, FluidRegistry.WATER);
	}

	public static BlockPos followFluidStreamToSourceBlock(World world, BlockPos blockPos, Fluid fluid)
	{
		return followFluidStreamToSourceBlock(world, blockPos, fluid, new HashSet<BlockPos>());
	}

	public static BlockPos followFluidStreamToSourceBlock(World world, BlockPos blockPos, Fluid fluid, Set<BlockPos> blocksChecked)
	{
		if (fluid.getBlock() instanceof BlockFluidFinite || FluidHelper.getFluidLevel(world, blockPos) == FluidHelper.getStillFluidLevel(fluid))
			return blockPos;

		List<BlockPos> blocksToCheck = new ArrayList<BlockPos>();
		blocksToCheck.add(blockPos.up());
		blocksToCheck.addAll(Arrays.asList(getBlocksAdjacentTo(blockPos)));

		for (BlockPos blockToCheck : blocksToCheck)
		{
			if (FluidHelper.getFluidTypeOfBlock(world.getBlockState(blockToCheck)) == fluid && !blocksChecked.contains(blockToCheck))
			{
				if (FluidHelper.getFluidLevel(world, blockToCheck) == FluidHelper.getStillFluidLevel(fluid))
					return blockToCheck;
				else
				{
					blocksChecked.add(blockToCheck);
					BlockPos foundSourceBlock = followFluidStreamToSourceBlock(world, blockToCheck, fluid, blocksChecked);

					if (foundSourceBlock != null)
						return foundSourceBlock;
				}
			}
		}
		return null;
	}

	public static BlockPos[] getBlocksInRadiusAround(BlockPos centerBlock, int radius)
	{
		Set<BlockPos> blocks = new HashSet<BlockPos>();
		int radiusSq = radius * radius;
		for (int xOffset = 0; xOffset <= radius; xOffset++)
		{
			for (int yOffset = 0; yOffset <= radius; yOffset++)
			{
				for (int zOffset = 0; zOffset <= radius; zOffset++)
				{
					BlockPos block = centerBlock.add(xOffset, yOffset, zOffset);
					int xDelta = block.getX() - centerBlock.getX();
					int yDelta = block.getY() - centerBlock.getY();
					int zDelta = block.getZ() - centerBlock.getZ();
					int deltaLengthSq = xDelta * xDelta + yDelta * yDelta + zDelta * zDelta;
					if (deltaLengthSq <= radiusSq)
					{
						blocks.add(block);
						blocks.add(centerBlock.add(-xOffset, yOffset, zOffset));
						blocks.add(centerBlock.add(xOffset, yOffset, -zOffset));
						blocks.add(centerBlock.add(-xOffset, yOffset, -zOffset));
						blocks.add(centerBlock.add(xOffset, -yOffset, zOffset));
						blocks.add(centerBlock.add(xOffset, -yOffset, -zOffset));
						blocks.add(centerBlock.add(-xOffset, -yOffset, zOffset));
						blocks.add(centerBlock.add(-xOffset, -yOffset, -zOffset));
					}
				}
			}
		}
		return blocks.toArray(new BlockPos[0]);
	}

	public static BlockPos[] filterBlockListToBreakableBlocks(World world, BlockPos... blocks)
	{
		List<BlockPos> filteredBlocks = new ArrayList<BlockPos>();
		for (BlockPos blockPos : blocks)
		{
			IBlockState state = world.getBlockState(blockPos);
			Block block = state.getBlock();

			if (block == null)
				continue;

			if (block.isAir(state, world, blockPos))
				continue;

			if (isBlockUnbreakable(world, blockPos))
				continue;

			if (state.getMaterial().isLiquid())
				continue;

			filteredBlocks.add(blockPos);
		}
		return filteredBlocks.toArray(new BlockPos[0]);
	}

	public static boolean isBlockUnbreakable(World world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlockHardness(world, pos) == BLOCK_HARDNESS_UNBREAKABLE;
	}

	public static TileEntity getTileEntitySafely(IBlockAccess world, BlockPos pos)
	{
		return world instanceof ChunkCache ? ((ChunkCache) world).func_190300_a(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
	}
}
