package squeek.veganoption.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import squeek.veganoption.inventory.ContainerComposter;

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
		EntityPlayer player = ctx.getServerHandler().player;
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
