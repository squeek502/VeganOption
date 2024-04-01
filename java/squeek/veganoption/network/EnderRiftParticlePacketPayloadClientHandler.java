package squeek.veganoption.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.helpers.RandomHelper;

public class EnderRiftParticlePacketPayloadClientHandler
{
	@OnlyIn(Dist.CLIENT)
	public static void handle(EnderRiftParticlePacketPayload payload, PlayPayloadContext context)
	{
		context.workHandler().submitAsync(() -> {
			if (context.level().isPresent())
				BlockEnderRift.spawnBlockTeleportFX(context.level().orElseThrow(), payload.x(), payload.y(), payload.z(), RandomHelper.random);
		});
	}
}
