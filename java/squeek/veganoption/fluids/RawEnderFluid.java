package squeek.veganoption.fluids;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
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
