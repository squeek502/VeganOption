package squeek.veganoption.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.simple.SimpleMessage;
import squeek.veganoption.gui.ComposterMenu;

public class MessageComposterTumble implements SimpleMessage
{
	public MessageComposterTumble(FriendlyByteBuf buf)
	{
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
	}

	public static void onMessage(MessageComposterTumble message, NetworkEvent.Context ctx)
	{
		Player player = ctx.getSender();
		if (player.hasContainerOpen() && player.containerMenu instanceof ComposterMenu)
		{
			ComposterMenu container = (ComposterMenu) player.containerMenu;
			if (!container.composter.isAerating())
			{
				container.composter.aerate();
			}
		}
	}
}
