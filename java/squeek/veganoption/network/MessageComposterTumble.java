package squeek.veganoption.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import squeek.veganoption.inventory.ContainerComposter;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageComposterTumble implements IMessage, IMessageHandler<MessageComposterTumble, IMessage>
{

	public MessageComposterTumble()
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public IMessage onMessage(MessageComposterTumble message, MessageContext ctx)
	{
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		if (player.openContainer != null && player.openContainer instanceof ContainerComposter)
		{
			ContainerComposter container = (ContainerComposter) player.openContainer;
			if (!container.composter.isAerating())
			{
				container.composter.aerate();
			}
		}

		return null;
	}
}
