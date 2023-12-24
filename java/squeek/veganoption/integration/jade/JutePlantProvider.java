package squeek.veganoption.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockJutePlant;
import squeek.veganoption.helpers.LangHelper;

public class JutePlantProvider implements IBlockComponentProvider
{
	private static final ResourceLocation UID = new ResourceLocation(ModInfo.MODID_LOWER, "jute_growth");
	private static JutePlantProvider INSTANCE;

	static JutePlantProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new JutePlantProvider();
		return INSTANCE;
	}

	private JutePlantProvider() {}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
	{
		if (config.get(Identifiers.MC_CROP_PROGRESS))
		{
			float growthValue = ((BlockJutePlant) accessor.getBlock()).getGrowthPercent(accessor.getLevel(), accessor.getPosition(), accessor.getBlockState());
			if (growthValue < 1f)
				VeganOptionPlugin.addPercentInfoToTooltip(tooltip, "waila.jute_growth", growthValue);
			else
				tooltip.add(Component.translatable(LangHelper.prependModId("waila.jute_growth"), IThemeHelper.get().success("waila.jute_growth.mature")));
		}
	}

	@Override
	public ResourceLocation getUid()
	{
		return UID;
	}

	@Override
	public boolean isRequired()
	{
		// Toggleable through the native Jade crop progress option, rather than our own config option.
		return true;
	}
}
