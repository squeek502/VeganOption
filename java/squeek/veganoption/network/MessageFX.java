package squeek.veganoption.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.content.modules.Egg;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.helpers.NetworkHelper;
import squeek.veganoption.helpers.RandomHelper;

public class MessageFX implements IMessage, IMessageHandler<MessageFX, IMessage>
{
	public enum FX
	{
		BUBBLE_POP,
		BLOCK_TELEPORT,
		PLASTIC_EGG_BREAK
	}

	double x, y, z;
	FX fx;

	public MessageFX()
	{
	}

	public MessageFX(double x, double y, double z, FX fx)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.fx = fx;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeByte(fx.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.fx = FX.values()[buf.readByte()];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageFX message, MessageContext ctx)
	{
		switch (message.fx)
		{
			case BUBBLE_POP:
				doEntityBreakFX(message, ctx, FrozenBubble.frozenBubble);
				break;
			case BLOCK_TELEPORT:
				BlockEnderRift.spawnBlockTeleportFX(NetworkHelper.getSidedWorld(ctx), message.x, message.y, message.z, RandomHelper.random);
				break;
			case PLASTIC_EGG_BREAK:
				doEntityBreakFX(message, ctx, Egg.plasticEgg);
				break;
			default:
				break;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void doEntityBreakFX(MessageFX message, MessageContext ctx, Item breakFXItem)
	{
		doEntityBreakFX(message, ctx, breakFXItem, 8);
	}

	@SideOnly(Side.CLIENT)
	public void doEntityBreakFX(MessageFX message, MessageContext ctx, Item breakFXItem, int iterations)
	{
		doEntityBreakFX(NetworkHelper.getSidedWorld(ctx), message.x, message.y, message.z, breakFXItem, iterations);
	}

	@SideOnly(Side.CLIENT)
	public void doEntityBreakFX(World world, double x, double y, double z, Item breakFXItem, int iterations)
	{
		Minecraft mc = Minecraft.getMinecraft();
		for (int i = 0; i < iterations; ++i)
		{
			// random velocity taken from EntityEgg
			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, ((double)RandomHelper.random.nextFloat() - 0.5D) * 0.08D, ((double)RandomHelper.random.nextFloat() - 0.5D) * 0.08D, ((double)RandomHelper.random.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(breakFXItem));
		}
	}
}
