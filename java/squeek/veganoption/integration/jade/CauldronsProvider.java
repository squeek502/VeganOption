package squeek.veganoption.integration.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.ui.ItemStackElement;
import squeek.veganoption.ModInfo;

public class CauldronsProvider implements IBlockComponentProvider
{
	private static final ResourceLocation UID = new ResourceLocation(ModInfo.MODID_LOWER, "basin");
	private static CauldronsProvider INSTANCE;

	static CauldronsProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new CauldronsProvider();
		return INSTANCE;
	}

	private CauldronsProvider() {}

	@Override
	public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon)
	{
		return ItemStackElement.of(new ItemStack(Items.CAULDRON));
	}

	@Override
	public boolean isRequired()
	{
		return true;
	}

	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig)
	{
		// noop
	}

	@Override
	public ResourceLocation getUid()
	{
		return UID;
	}
}
