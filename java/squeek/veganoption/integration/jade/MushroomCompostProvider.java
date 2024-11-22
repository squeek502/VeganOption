package squeek.veganoption.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.MushroomCompostBlock;
import squeek.veganoption.helpers.LangHelper;

public class MushroomCompostProvider implements IBlockComponentProvider
{
	private static final ResourceLocation UID = new ResourceLocation(ModInfo.MODID_LOWER, "mushroom_compost");
	private static MushroomCompostProvider INSTANCE;

	static MushroomCompostProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new MushroomCompostProvider();
		return INSTANCE;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig)
	{
		MushroomCompostBlock.Variant variant = blockAccessor.getBlockState().getValue(MushroomCompostBlock.VARIANT);
		tooltip.add(Component.translatable(LangHelper.prependModId("waila.mushroom_spores"), LangHelper.translateRaw(variant.getItem().get().getDescriptionId())));
	}

	@Override
	public ResourceLocation getUid()
	{
		return UID;
	}
}
