package squeek.veganoption.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.simple.SimpleMessage;

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

	public static void onMessage(EnderRiftParticleMessage message, NetworkEvent.Context ctx)
	{
		//noinspection removal
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EnderRiftParticleMessageClientHandler.handle(message, ctx)));
		ctx.setPacketHandled(true);
	}
}
