package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class CauldronsProvider implements IBlockComponentProvider
{
	private static CauldronsProvider INSTANCE;

	public static CauldronsProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new CauldronsProvider();
		return INSTANCE;
	}

	private CauldronsProvider() {}

	@Override
	public @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config)
	{
		return new ItemComponent(Items.CAULDRON);
	}
}
