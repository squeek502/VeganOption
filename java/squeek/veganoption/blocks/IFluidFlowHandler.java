package squeek.veganoption.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFluidFlowHandler
{
	boolean onFluidFlowInto(World world, BlockPos pos, int flowDecay);
}
