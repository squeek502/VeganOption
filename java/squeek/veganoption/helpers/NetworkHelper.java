package squeek.veganoption.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
