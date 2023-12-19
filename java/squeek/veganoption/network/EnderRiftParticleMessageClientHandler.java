package squeek.veganoption.network;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.NetworkEvent;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.helpers.RandomHelper;

public class EnderRiftParticleMessageClientHandler
{
	@OnlyIn(Dist.CLIENT)
	public static void handle(EnderRiftParticleMessage message, NetworkEvent.Context ctx)
	{
		BlockEnderRift.spawnBlockTeleportFX(Minecraft.getInstance().level, message.x, message.y, message.z, RandomHelper.random);
	}
}
