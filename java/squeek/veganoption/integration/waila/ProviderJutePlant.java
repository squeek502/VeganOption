package squeek.veganoption.integration.waila;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import squeek.veganoption.blocks.BlockJutePlant;

public class ProviderJutePlant implements IWailaDataProvider
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
		if (config.getConfig("general.showcrop"))
		{
			int x = accessor.getPosition().blockX;
			int y = accessor.getPosition().blockY;
			int z = accessor.getPosition().blockZ;
			float growthValue = ((BlockJutePlant) accessor.getBlock()).getGrowthPercent(accessor.getWorld(), x, y, z) * 100.0F;
			if (growthValue < 100)
				toolTip.add(String.format("%s : %.0f %%", StatCollector.translateToLocal("hud.msg.growth"), growthValue));
			else
				toolTip.add(String.format("%s : %s", StatCollector.translateToLocal("hud.msg.growth"), StatCollector.translateToLocal("hud.msg.mature")));
		}

		return toolTip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}
}
