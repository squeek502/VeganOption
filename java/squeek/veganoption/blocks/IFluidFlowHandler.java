package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import squeek.veganoption.mixins.FlowingFluidMixin;

/**
 * Implement this interface on Block classes to prevent fluids from flowing and affecting the implementing block.
 */
public interface IFluidFlowHandler
{
	/**
	 * True to halt fluid mechanics. Called from a mixin of {@link FlowingFluid#spreadTo(LevelAccessor, BlockPos, BlockState, Direction, FluidState)}
	 * at {@link FlowingFluidMixin}.
	 */
	boolean onFluidFlowInto(LevelAccessor world, BlockPos pos, int amount);
}
