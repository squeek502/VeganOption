package squeek.veganoption.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.helpers.LangHelper;

public class BasinProvider implements IBlockComponentProvider
{
	private static final ResourceLocation UID = new ResourceLocation(ModInfo.MODID_LOWER, "basin");
	private static BasinProvider INSTANCE;

	static BasinProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new BasinProvider();
		return INSTANCE;
	}

	private BasinProvider() {}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
	{
		TileEntityBasin basin = (TileEntityBasin) accessor.getBlockEntity();
		tooltip.add(Component.translatable(LangHelper.prependModId("waila.basin." + (basin.isOpen() ? "open" : "closed"))));
	}

	@Override
	public ResourceLocation getUid()
	{
		return UID;
	}
}
