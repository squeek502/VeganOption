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
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.helpers.LangHelper;

public class ProviderRettable implements IWailaDataProvider
{
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
		BlockRettable blockRettable = (BlockRettable) accessor.getBlock();
		float rettingPercent = blockRettable.getRettingPercent(accessor.getWorld(), accessor.getPosition());
		if (rettingPercent >= 1)
			toolTip.add(LangHelper.translate("waila.retted"));
		else
		{
			if (blockRettable.canRet(accessor.getWorld(), accessor.getPosition()))
				toolTip.add(LangHelper.translate("waila.retting") + " : " + (int) (rettingPercent * 100f) + "%");
			else
				toolTip.add(LangHelper.translate("waila.retting.not.submerged"));
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
		return null;
	}
}
