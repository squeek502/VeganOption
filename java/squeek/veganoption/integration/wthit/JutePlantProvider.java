package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.blocks.BlockJutePlant;

public class JutePlantProvider implements IBlockComponentProvider
{
	private static final ResourceLocation CONFIG_ID = new ResourceLocation("crop_progress");

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config)
	{
		if (config.getBoolean(CONFIG_ID))
		{
			float growthValue = ((BlockJutePlant) accessor.getBlock()).getGrowthPercent(accessor.getWorld(), accessor.getPosition(), accessor.getBlockState());
			VeganOptionPlugin.addPercentInfoToTooltip(tooltip, "waila.jute_growth", growthValue);
		}
	}
}
