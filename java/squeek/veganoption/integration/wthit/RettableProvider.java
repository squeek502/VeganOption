package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.helpers.LangHelper;

public class RettableProvider implements IBlockComponentProvider
{
	static final ResourceLocation CONFIG_ID = new ResourceLocation(ModInfo.MODID_LOWER, "retting");

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config)
	{
		if (config.getBoolean(CONFIG_ID))
		{
			BlockRettable blockRettable = (BlockRettable) accessor.getBlock();
			float rettingPercent = BlockRettable.getRettingPercent(accessor.getBlockState());
			if (rettingPercent >= 1)
			{
				tooltip.addLine(Component.translatable(LangHelper.prependModId("waila.retted")));
			}
			else
			{
				if (blockRettable.canRet(accessor.getWorld(), accessor.getPosition()))
					VeganOptionPlugin.addPercentInfoToTooltip(tooltip, "waila.retting", rettingPercent);
				else
					tooltip.addLine(Component.translatable(LangHelper.prependModId("waila.retting.not_submerged")));
			}
		}
	}
}
