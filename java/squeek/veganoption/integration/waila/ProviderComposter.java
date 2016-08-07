package squeek.veganoption.integration.waila;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.helpers.LangHelper;

public class ProviderComposter implements IWailaDataProvider
{
	public static final String DEGREE_SYMBOL = "\u00B0";

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		NBTTagCompound tag = accessor.getNBTData();

		if (tag.getLong("Start") == TileEntityComposter.NOT_COMPOSTING)
		{
			toolTip.add(LangHelper.translate("waila.composter.empty"));
		}
		else
		{
			toolTip.add(String.format("%s : %d%%", LangHelper.translate("waila.composter.composting"), (int) (tag.getFloat("Compost") * 100f)));
			toolTip.add(String.format("%s : %.0f" + DEGREE_SYMBOL + "C", LangHelper.translate("waila.composter.temperature"), tag.getFloat("Temperature")));
		}
		return toolTip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
	{
		TileEntityComposter tile = (TileEntityComposter) te;
		tag.setLong("Start", tile.compostStart);
		tag.setFloat("Temperature", tile.getCompostTemperature());
		tag.setFloat("Start", tile.getCompostingPercent());
		return tag;
	}
}
