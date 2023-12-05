package squeek.veganoption.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.veganoption.api.event.PistonEvent;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin extends DirectionalBlock
{
	protected PistonBaseBlockMixin(Properties properties)
	{
		super(properties);
	}

	@Inject(method = "moveBlocks", at = @At("HEAD"))
	private void veganoption$onMoveBlocks(Level level, BlockPos pos, Direction direction, boolean extending, CallbackInfoReturnable<Boolean> info)
	{
		if (extending)
			NeoForge.EVENT_BUS.post(new PistonEvent.TryExtend(level, pos, level.getBlockState(pos).getValue(PistonBaseBlock.FACING)));
	}
}
