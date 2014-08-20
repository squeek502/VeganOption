package squeek.veganoption.integration;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import squeek.veganoption.blocks.BlockRettable;

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
	}
}