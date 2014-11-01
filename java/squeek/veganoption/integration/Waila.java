package squeek.veganoption.integration;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.blocks.tiles.TileEntityComposter;

public class Waila implements IWailaDataProvider
{
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		if (accessor.getBlock() instanceof BlockRettable)
		{
			BlockRettable blockRettable = (BlockRettable) accessor.getBlock();
			float rettingPercent = (float) blockRettable.getRettingPercentFromMeta(accessor.getMetadata());
			if (rettingPercent >= 1)
				toolTip.add("Retted");
			else
			{
				toolTip.add("Retting : " + (int) (rettingPercent * 100f) + "%");
				if (!blockRettable.canRet(accessor.getWorld(), accessor.getPosition().blockX, accessor.getPosition().blockY, accessor.getPosition().blockZ))
					toolTip.add("Needs to be submerged in water");
			}
		}
		else if (accessor.getTileEntity() instanceof TileEntityComposter)
		{
			NBTTagCompound tag = accessor.getNBTData();

			if (tag.getLong("Start") == TileEntityComposter.NOT_COMPOSTING)
			{
				toolTip.add("Empty");
			}
			else
			{
				toolTip.add(String.format("%s : %.0f%%", "Composting", tag.getFloat("Compost") * 100));
				toolTip.add(String.format("%s : %.0f°C", "Temperature", tag.getFloat("Temperature")));
			}
		}
		return toolTip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		Waila instance = new Waila();

		registrar.registerBodyProvider(instance, BlockRettable.class);
		registrar.registerBodyProvider(instance, TileEntityComposter.class);
		// necessary to allow specifying specific keys to sync
		registrar.registerSyncedNBTKey("x", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("y", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("z", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("Compost", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("Temperature", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("Start", TileEntityComposter.class);
	}
}