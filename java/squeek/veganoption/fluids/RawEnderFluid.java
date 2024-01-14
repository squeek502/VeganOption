package squeek.veganoption.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import squeek.veganoption.blocks.BlockRawEnder;
import squeek.veganoption.content.modules.Ender;

public abstract class RawEnderFluid extends BaseFlowingFluid
{
	protected RawEnderFluid(Properties properties)
	{
		super(properties);
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state)
	{
		return Ender.rawEnderBlock.get().defaultBlockState()
			.setValue(LiquidBlock.LEVEL, 8 - state.getAmount() + (state.getValue(FALLING) ? 8 : 0))
			.setValue(BlockRawEnder.IS_SOURCE, state.isSource());
	}

	@Override
	protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
	{
		builder.add(FALLING);
		builder.add(LEVEL);
	}

	@Override
	public int getAmount(FluidState state)
	{
		return state.getValue(LEVEL);
	}

	// Copied from FlowingFluid
	@Override
	protected FluidState getNewLiquid(Level level, BlockPos pos, BlockState state)
	{
		int i = 0;
		int j = 0;

		for (Direction direction : Direction.Plane.HORIZONTAL)
		{
			BlockPos blockpos = pos.relative(direction);
			BlockState blockstate = level.getBlockState(blockpos);
			FluidState fluidstate = blockstate.getFluidState();
			if (fluidstate.getType().isSame(this) && this.canPassThroughWall(direction, level, pos, state, blockpos, blockstate))
			{
				if (fluidstate.isSource() && net.neoforged.neoforge.event.EventHooks.canCreateFluidSource(level, blockpos, blockstate, fluidstate.canConvertToSource(level, blockpos)))
					++j;

				i = Math.max(i, fluidstate.getAmount());
			}
		}

		if (j >= 2)
		{
			BlockState blockstate1 = level.getBlockState(pos.below());
			FluidState fluidstate1 = blockstate1.getFluidState();
			if (blockstate1.isSolid() || this.isSourceBlockOfThisType(fluidstate1))
				return this.getSource(false);
		}

		BlockPos blockpos1 = pos.above();
		BlockState blockstate2 = level.getBlockState(blockpos1);
		FluidState fluidstate2 = blockstate2.getFluidState();
		if (!fluidstate2.isEmpty() && fluidstate2.getType().isSame(this) && this.canPassThroughWall(Direction.UP, level, pos, state, blockpos1, blockstate2))
			// This line is changed from default implementation
			return this.getFlowing(getAmount(fluidstate2), true);
		else
		{
			int k = i - this.getDropOff(level);
			return k <= 0 ? Fluids.EMPTY.defaultFluidState() : this.getFlowing(k, false);
		}
	}

	public static class Flowing extends RawEnderFluid
	{
		public Flowing(Properties properties)
		{
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 1).setValue(FALLING, false));
		}

		@Override
		public boolean isSource(FluidState state)
		{
			return false;
		}
	}

	public static class Still extends RawEnderFluid
	{
		public Still(Properties properties)
		{
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 8).setValue(FALLING, false));
		}

		@Override
		public boolean isSource(FluidState state)
		{
			return true;
		}
	}
}
