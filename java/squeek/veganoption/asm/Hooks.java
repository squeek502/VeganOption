package squeek.veganoption.asm;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.registry.Content;

public class Hooks
{
	// return true to stop the default code from being executed
	public static boolean onFlowIntoBlock(World world, int x, int y, int z, int flowDecay)
	{
		Block block = world.getBlock(x, y, z);
		if (block == Content.enderRift)
			return ((BlockEnderRift) block).onFluidFlowInto(world, x, y, z, flowDecay);
		else
			return false;
	}
}
