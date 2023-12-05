package squeek.veganoption.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.simple.SimpleMessage;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.helpers.RandomHelper;

public class EnderRiftParticleMessage implements SimpleMessage
{
	double x, y, z;

	public EnderRiftParticleMessage(FriendlyByteBuf buf)
	{
		this(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	public EnderRiftParticleMessage(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	@OnlyIn(Dist.CLIENT)
	public static void onMessage(EnderRiftParticleMessage message, NetworkEvent.Context ctx)
	{
		ctx.enqueueWork(() -> BlockEnderRift.spawnBlockTeleportFX(ctx.getSender().serverLevel(), message.x, message.y, message.z, RandomHelper.random));
		ctx.setPacketHandled(true);
	}
}
