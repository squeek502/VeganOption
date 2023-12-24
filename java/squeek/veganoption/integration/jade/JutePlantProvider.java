package squeek.veganoption.integration.jade;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockJutePlant;

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
			VeganOptionPlugin.addPercentInfoToTooltip(tooltip, "waila.jute_growth", growthValue);
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
