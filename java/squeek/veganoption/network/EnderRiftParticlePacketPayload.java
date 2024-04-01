package squeek.veganoption.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.ModInfo;

public record EnderRiftParticlePacketPayload(double x, double y, double z) implements CustomPacketPayload
{
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.MODID_LOWER, "ender_rift_particle");

	public EnderRiftParticlePacketPayload(FriendlyByteBuf buf)
	{
		this(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	@Override
	public ResourceLocation id()
	{
		return ID;
	}
}
