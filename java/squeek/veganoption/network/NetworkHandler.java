package squeek.veganoption.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import squeek.veganoption.ModInfo;

public class NetworkHandler
{
	public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MODID);

	public static void init()
	{
		channel.registerMessage(MessageFX.class, MessageFX.class, 0, Side.CLIENT);
		channel.registerMessage(MessageComposterTumble.class, MessageComposterTumble.class, 1, Side.SERVER);
	}
}
