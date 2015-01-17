package squeek.veganoption.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.helpers.RandomHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageBlockTeleport implements IMessage, IMessageHandler<MessageBlockTeleport, IMessage>
{
	double x, y, z;

	public MessageBlockTeleport()
	{
	}

	public MessageBlockTeleport(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	@Override
	public IMessage onMessage(MessageBlockTeleport message, MessageContext ctx)
	{	
		Minecraft mc = Minecraft.getMinecraft();
		BlockEnderRift.spawnBlockTeleportFX((World) mc.theWorld, message.x, message.y, message.z, RandomHelper.random);
		return null;
	}
}
