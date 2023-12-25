package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.helpers.LangHelper;

// todo: hide inventory contents
public class ComposterProvider implements IBlockComponentProvider, IDataProvider<TileEntityComposter>
{
	static final ResourceLocation CONFIG_ID = new ResourceLocation(ModInfo.MODID_LOWER, "composter");
	private static final String DATA_PERCENT = "Percent";
	private static final String DATA_TEMPERATURE = "Temperature";
	private static final String DATA_COMPOSTING = "IsComposting";
	private static ComposterProvider INSTANCE;

	static ComposterProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new ComposterProvider();
		return INSTANCE;
	}

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config)
	{
		if (config.getBoolean(CONFIG_ID))
		{
			CompoundTag data = accessor.getData().raw();
			if (data.getBoolean(DATA_COMPOSTING))
			{
				VeganOptionPlugin.addPercentInfoToTooltip(tooltip, "waila.composter.percent", data.getFloat(DATA_PERCENT));
				tooltip.addLine(Component.translatable(LangHelper.prependModId("waila.composter.temperature"), String.format("%1$dºC", (int) Math.floor(data.getFloat(DATA_TEMPERATURE)))));
			}
			else
			{
				tooltip.addLine(Component.translatable(LangHelper.prependModId("waila.composter.empty")));
			}
		}
	}

	@Override
	public void appendData(IDataWriter data, IServerAccessor<TileEntityComposter> accessor, IPluginConfig config)
	{
		if (config.getBoolean(CONFIG_ID))
		{
			TileEntityComposter te = accessor.getTarget();
			data.raw().putBoolean(DATA_COMPOSTING, te.isComposting());
			data.raw().putFloat(DATA_PERCENT, te.getCompostingPercent());
			data.raw().putFloat(DATA_TEMPERATURE, te.getCompostTemperature());
		}
	}
}
