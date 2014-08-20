package squeek.veganoption.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockHelper
{
	public static boolean isAdjacentToOrCoveredInWater(World world, int x, int y, int z)
	{
		return world.getBlock(x, y + 1, z).getMaterial() == Material.water || isAdjacentToWater(world, x, y, z);
	}

	public static boolean isAdjacentToWater(World world, int x, int y, int z)
	{
		return isAdjacentToMaterial(world, x, y, z, Material.water);
	}

	public static boolean isAdjacentToMaterial(World world, int x, int y, int z, Material material)
	{
		for (Block block : getBlocksAdjacentTo(world, x, y, z))
		{
			if (block.getMaterial() == material)
				return true;
		}
		return false;
	}

	public static Block[] getBlocksAdjacentTo(World world, int x, int y, int z)
	{
		return new Block[]{
		world.getBlock(x - 1, y, z), world.getBlock(x + 1, y, z),
		world.getBlock(x, y, z - 1), world.getBlock(x, y, z + 1)
		};
	}
}
