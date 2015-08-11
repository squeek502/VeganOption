package squeek.veganoption.blocks;

import net.minecraft.world.World;

public interface IFluidFlowHandler
{
	public boolean onFluidFlowInto(World world, int x, int y, int z, int flowDecay);
}
