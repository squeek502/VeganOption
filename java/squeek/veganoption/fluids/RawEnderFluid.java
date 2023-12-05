package squeek.veganoption.fluids;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import squeek.veganoption.content.modules.Ender;

public abstract class RawEnderFluid extends BaseFlowingFluid
{
	protected RawEnderFluid()
	{
		super(new Properties(() -> Ender.rawEnderFluidType.get(), () -> Ender.rawEnderStill.get(), () -> Ender.rawEnderFlowing.get())
			.bucket(() -> Ender.rawEnderBucket.get())
			.block(() -> (LiquidBlock) Ender.rawEnderBlock.get()));
	}

	public static class Flowing extends RawEnderFluid
	{
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
		{
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state)
		{
			return false;
		}

		@Override
		public int getAmount(FluidState state)
		{
			return state.getValue(LEVEL);
		}
	}

	public static class Still extends RawEnderFluid
	{
		@Override
		public boolean isSource(FluidState state)
		{
			return false;
		}

		@Override
		public int getAmount(FluidState state)
		{
			return 8;
		}
	}
}
