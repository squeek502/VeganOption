package squeek.veganoption.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import snownee.jade.api.*;
import snownee.jade.api.theme.IThemeHelper;
import squeek.veganoption.blocks.*;
import squeek.veganoption.blocks.tiles.ComposterBlockEntity;
import squeek.veganoption.helpers.LangHelper;

@WailaPlugin
public class VeganOptionPlugin implements IWailaPlugin
{
	@Override
	public void registerClient(IWailaClientRegistration registerer)
	{
		registerer.registerBlockComponent(RettableProvider.getInstance(), RettableBlock.class);
		registerer.registerBlockComponent(ComposterProvider.getInstance(), ComposterBlock.class);
		registerer.registerBlockComponent(BasinProvider.getInstance(), BasinBlock.class);
		registerer.registerBlockComponent(JutePlantProvider.getInstance(), JutePlantBlock.class);
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
		registerer.registerItemStorage(ComposterProvider.HideInventory.getInstance(), ComposterBlockEntity.class);
		registerer.registerBlockDataProvider(ComposterProvider.getInstance(), ComposterBlockEntity.class);
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
