package squeek.veganoption.mixins;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.veganoption.content.modules.Syrup;

@OnlyIn(Dist.CLIENT)
@Mixin(BubbleParticle.class)
public abstract class BubbleParticleMixin extends TextureSheetParticle
{
	protected BubbleParticleMixin(ClientLevel level, double x, double y, double z)
	{
		super(level, x, y, z);
	}

	protected BubbleParticleMixin(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	{
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/BubbleParticle;remove()V", ordinal = 1), cancellable = true)
	public void onRemovalDueToInvalidBlockState(CallbackInfo ci)
	{
		// Prevents the removal of the particle due to not being a water fluidstate.
		if (level.getBlockState(BlockPos.containing(x, y, z)).is(Syrup.sapCauldron.get()))
			ci.cancel();
	}
}
