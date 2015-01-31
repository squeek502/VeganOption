package squeek.veganoption.asm;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.modules.Egg;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.integration.tic.TConstruct;

public class Hooks
{
	// return true to stop the default code from being executed
	public static boolean onFlowIntoBlock(World world, int x, int y, int z, int flowDecay)
	{
		Block block = world.getBlock(x, y, z);
		if (block == Ender.enderRift)
			return ((BlockEnderRift) block).onFluidFlowInto(world, x, y, z, flowDecay);
		else
			return false;
	}

	// return true to stop the default code from being executed
	public static boolean onEntityItemUpdate(EntityItem entityItem)
	{
		if (!entityItem.worldObj.isRemote && entityItem.isCollided && entityItem.getEntityItem().getItem() == Items.potato)
		{
			if (entityItem.worldObj.getBlock(MathHelper.floor_double(entityItem.posX), MathHelper.floor_double(entityItem.posY), MathHelper.floor_double(entityItem.posZ)) == Blocks.piston_head)
			{
				int stackSize = entityItem.getEntityItem().stackSize;
				entityItem.setEntityItemStack(new ItemStack(Egg.potatoStarch, stackSize));
				return true;
			}
		}
		return false;
	}

	// return the TiC ToolRod version of the given handle if it exists
	public static ItemStack getRealHandle(ItemStack handle)
	{
		if (IntegrationHandler.integrationExists(IntegrationHandler.MODID_TINKERS_CONSTRUCT))
			return TConstruct.getRealHandle(handle);

		return handle;
	}
	
	// helper function for Mystcraft linking book recipe compatibility
	public static boolean isLeather(ItemStack itemStack)
	{
		if (itemStack == null)
			return false;

		int[] oreIDs = OreDictionary.getOreIDs(itemStack);
		for (int oreID : oreIDs)
		{
			if (OreDictionary.getOreName(oreID).equals(ContentHelper.leatherOreDict))
				return true;
		}
		return false;
	}
}
