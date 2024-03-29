package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import squeek.veganoption.blocks.*;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.helpers.LangHelper;

public class VeganOptionPlugin implements IWailaPlugin
{
	@Override
	public void register(IRegistrar registrar)
	{
		registrar.addComponent(new BasinProvider(), TooltipPosition.BODY, BlockBasin.class);
		registrar.addComponent(ComposterProvider.getInstance(), TooltipPosition.BODY, BlockComposter.class);
		registrar.addBlockData(ComposterProvider.getInstance(), TileEntityComposter.class);
		registrar.addComponent(new JutePlantProvider(), TooltipPosition.BODY, BlockJutePlant.class);
		registrar.addComponent(new RettableProvider(), TooltipPosition.BODY, BlockRettable.class);
		registrar.addIcon(CauldronsProvider.getInstance(), SapCauldronBlock.class);
		registrar.addIcon(CauldronsProvider.getInstance(), LayeredCauldronBlock.class);

		registrar.addConfig(BasinProvider.CONFIG_ID, true);
		registrar.addSyncedConfig(ComposterProvider.CONFIG_ID, true, false);
		registrar.addConfig(RettableProvider.CONFIG_ID, true);
	}

	static void addPercentInfoToTooltip(ITooltip tooltip, String key, float value)
	{
		tooltip.addLine(Component.translatable(LangHelper.prependModId(key), String.format("%1$d%%", Math.round(value * 100f))));
	}
}
