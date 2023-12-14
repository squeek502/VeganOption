package squeek.veganoption.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.veganoption.blocks.IFluidFlowHandler;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid
{
	@Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
	protected void veganoption$onSpreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo info)
	{
		Block block = level.getBlockState(pos).getBlock();
		if (block instanceof IFluidFlowHandler handler && handler.onFluidFlowInto(level, pos, fluidState.getAmount()))
			info.cancel();
	}
}
