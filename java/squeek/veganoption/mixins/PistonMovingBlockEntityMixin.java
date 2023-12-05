package squeek.veganoption.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.veganoption.api.event.PistonEvent;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin extends BlockEntity
{
	public PistonMovingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private static void veganoption$onTick(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity piston, CallbackInfo info)
	{
		if (piston.isExtending())
			NeoForge.EVENT_BUS.post(new PistonEvent.Extending(level, pos, piston.progress));
	}
}
