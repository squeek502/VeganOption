package squeek.veganoption.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkHelper
{
	public static EntityPlayer getSidedPlayer(MessageContext ctx)
	{
		return ctx.side == Side.SERVER ? ctx.getServerHandler().playerEntity : getClientPlayer();
	}

	@SideOnly(Side.CLIENT)
	public static EntityPlayer getClientPlayer()
	{
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

	public static World getSidedWorld(MessageContext ctx)
	{
		return getSidedPlayer(ctx).worldObj;
	}
}
