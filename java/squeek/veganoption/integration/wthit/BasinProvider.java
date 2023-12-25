package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.helpers.LangHelper;

public class BasinProvider implements IBlockComponentProvider
{
	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config)
	{
		TileEntityBasin basin = accessor.getBlockEntity();
		if (basin != null)
			tooltip.addLine(Component.translatable(LangHelper.prependModId("waila.basin." + (basin.isOpen() ? "open" : "closed"))));
	}
}
