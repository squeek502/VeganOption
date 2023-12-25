package squeek.veganoption.integration.wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.resources.ResourceLocation;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockBasin;

public class VeganOptionPlugin implements IWailaPlugin
{
	@Override
	public void register(IRegistrar registrar)
	{
		registrar.addComponent(new BasinProvider(), TooltipPosition.BODY, BlockBasin.class);

		registrar.addConfig(new ResourceLocation(ModInfo.MODID_LOWER, "basin"), true);
	}
}
