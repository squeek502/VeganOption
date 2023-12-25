package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.helpers.LangHelper;

public class BasinProvider implements IBlockComponentProvider
{
	static final ResourceLocation CONFIG_ID = new ResourceLocation(ModInfo.MODID_LOWER, "basin");

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config)
	{
		if (config.getBoolean(CONFIG_ID))
		{
			TileEntityBasin basin = accessor.getBlockEntity();
			if (basin != null)
				tooltip.addLine(Component.translatable(LangHelper.prependModId("waila.basin." + (basin.isOpen() ? "open" : "closed"))));
		}
	}
}
