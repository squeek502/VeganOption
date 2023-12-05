package squeek.veganoption.network;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import squeek.veganoption.ModInfo;

public class NetworkHandler
{
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(ModInfo.MODID_LOWER, "main"),
		() -> PROTOCOL_VERSION,
		PROTOCOL_VERSION::equals,
		PROTOCOL_VERSION::equals);

	public static void init()
	{
		int index = 0;
		channel.registerMessage(++index, EnderRiftParticleMessage.class, EnderRiftParticleMessage::encode, EnderRiftParticleMessage::new, EnderRiftParticleMessage::onMessage);
		channel.registerMessage(++index, MessageComposterTumble.class, MessageComposterTumble::encode, MessageComposterTumble::new, MessageComposterTumble::onMessage);
	}
}
