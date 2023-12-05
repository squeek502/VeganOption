package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface IFluidFlowHandler
{
	boolean onFluidFlowInto(LevelAccessor world, BlockPos pos, int amount);
}
