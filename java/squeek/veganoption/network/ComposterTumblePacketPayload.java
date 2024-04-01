package squeek.veganoption.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.ModInfo;

public record ComposterTumblePacketPayload() implements CustomPacketPayload
{
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.MODID_LOWER, "composter_tumble");

	public ComposterTumblePacketPayload(FriendlyByteBuf buf)
	{
		this();
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		// no op
	}

	@Override
	public ResourceLocation id()
	{
		return ID;
	}
}
