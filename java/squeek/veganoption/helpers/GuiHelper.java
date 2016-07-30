package squeek.veganoption.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.gui.GuiComposter;
import squeek.veganoption.inventory.ContainerComposter;

public class GuiHelper implements IGuiHandler
{
	public static enum GuiIds
	{
		COMPOSTER
	}

	public static final int NINE_SLOT_WIDTH = 162;
	public static final int STANDARD_GUI_WIDTH = 176;
	public static final int STANDARD_SLOT_WIDTH = 18;

	public static void init()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(VeganOption.instance, new GuiHelper());
	}

	public static boolean openGuiOfTile(EntityPlayer player, TileEntity tile)
	{
		if (!player.worldObj.isRemote)
		{
			if (tile instanceof TileEntityComposter)
			{
				player.openGui(VeganOption.instance, GuiIds.COMPOSTER.ordinal(), tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
				return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
	{
		return getSidedGuiElement(false, guiId, player, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
	{
		return getSidedGuiElement(true, guiId, player, world, x, y, z);
	}

	public Object getSidedGuiElement(boolean isClientSide, int guiId, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (GuiIds.values()[guiId])
		{
			case COMPOSTER:
				TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
				if (tile != null && tile instanceof TileEntityComposter)
				{
					TileEntityComposter composter = (TileEntityComposter) tile;
					return isClientSide ? new GuiComposter(player.inventory, composter) : new ContainerComposter(player.inventory, composter);
				}
				break;
			default:
				break;
		}
		return null;
	}
}