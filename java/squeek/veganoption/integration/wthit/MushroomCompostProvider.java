package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.MushroomCompostBlock;
import squeek.veganoption.helpers.LangHelper;

public class MushroomCompostProvider implements IBlockComponentProvider
{
	static final ResourceLocation CONFIG_ID = new ResourceLocation(ModInfo.MODID_LOWER, "mushroom_compost");

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config)
	{
		if (config.getBoolean(CONFIG_ID))
		{
			MushroomCompostBlock.Variant variant = accessor.getBlockState().getValue(MushroomCompostBlock.VARIANT);
			tooltip.addLine(Component.translatable(LangHelper.prependModId("waila.mushroom_spores"), LangHelper.translateRaw(variant.getItem().get().getDescriptionId())));
		}
	}
}
