package squeek.veganoption.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.helpers.LangHelper;

public class RettableProvider implements IBlockComponentProvider
{
	static final ResourceLocation UID = new ResourceLocation(ModInfo.MODID_LOWER, "retting");
	private static RettableProvider INSTANCE;

	static RettableProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new RettableProvider();
		return INSTANCE;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig)
	{
		BlockRettable blockRettable = (BlockRettable) accessor.getBlock();
		float rettingPercent = BlockRettable.getRettingPercent(accessor.getBlockState());
		if (rettingPercent >= 1)
		{
			tooltip.add(IThemeHelper.get().success(Component.translatable(LangHelper.prependModId("waila.retted"))));
		}
		else
		{
			if (blockRettable.canRet(accessor.getLevel(), accessor.getPosition()))
				VeganOptionPlugin.addPercentInfoToTooltip(tooltip, "waila.retting", rettingPercent);
			else
				tooltip.add(Component.translatable(LangHelper.prependModId("waila.retting.not_submerged")));
		}
	}

	@Override
	public ResourceLocation getUid()
	{
		return UID;
	}
}
