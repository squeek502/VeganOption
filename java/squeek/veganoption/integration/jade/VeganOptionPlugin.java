package squeek.veganoption.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import snownee.jade.api.*;
import snownee.jade.api.theme.IThemeHelper;
import squeek.veganoption.blocks.*;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.helpers.LangHelper;

@WailaPlugin
public class VeganOptionPlugin implements IWailaPlugin
{
	@Override
	public void registerClient(IWailaClientRegistration registerer)
	{
		registerer.registerBlockComponent(RettableProvider.getInstance(), BlockRettable.class);
		registerer.registerBlockComponent(ComposterProvider.getInstance(), BlockComposter.class);
		registerer.registerBlockComponent(BasinProvider.getInstance(), BlockBasin.class);
		registerer.registerBlockComponent(JutePlantProvider.getInstance(), BlockJutePlant.class);
		registerer.registerBlockIcon(CauldronsProvider.getInstance(), SapCauldronBlock.class);
		registerer.registerBlockIcon(CauldronsProvider.getInstance(), LayeredCauldronBlock.class);

		registerer.markAsClientFeature(RettableProvider.getInstance().getUid());
		registerer.markAsClientFeature(BasinProvider.getInstance().getUid());
		registerer.markAsClientFeature(JutePlantProvider.getInstance().getUid());
		registerer.markAsClientFeature(CauldronsProvider.getInstance().getUid());
	}

	@Override
	public void register(IWailaCommonRegistration registerer)
	{
		registerer.registerItemStorage(ComposterProvider.HideInventory.getInstance(), TileEntityComposter.class);
		registerer.registerBlockDataProvider(ComposterProvider.getInstance(), TileEntityComposter.class);
	}

	static void addPercentInfoToTooltip(ITooltip tooltip, String key, float value)
	{
		addInfoToTooltip(tooltip, key, String.format("%1$d%%", Math.round(value * 100f)));
	}

	static void addInfoToTooltip(ITooltip tooltip, String key, Object arg)
	{
		tooltip.add(Component.translatable(LangHelper.prependModId(key), IThemeHelper.get().info(arg)));
	}
}
