package squeek.veganoption.helpers;

import java.lang.reflect.Method;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class InventoryHelper
{
	public static final Method hopperInsertIntoInventory = ReflectionHelper.findMethod(TileEntityHopper.class, null, new String[]{"func_145899_c", "c"}, IInventory.class, ItemStack.class, int.class, int.class);

	public static IInventory getInventoryAtLocation(World world, int x, int y, int z)
	{
		return TileEntityHopper.getInventoryAtPosition(world, x, y, z);
	}

	public static ItemStack insertStackIntoInventory(ItemStack itemStack, IInventory inventory)
	{
		return TileEntityHopper.putStackInInventoryAllSlots(inventory, itemStack, EnumFacing.DOWN);
	}

	public static float getPercentInventoryFilled(IInventory inventory)
	{
		if (inventory == null || inventory.getSizeInventory() == 0)
			return 0;

		float filledPercent = 0.0F;

		for (int slotNum = 0; slotNum < inventory.getSizeInventory(); ++slotNum)
		{
			ItemStack itemstack = inventory.getStackInSlot(slotNum);

			if (itemstack != null)
			{
				filledPercent += (float) itemstack.stackSize / (float) Math.min(inventory.getInventoryStackLimit(), itemstack.getMaxStackSize());
			}
		}

		filledPercent /= inventory.getSizeInventory();
		return filledPercent;
	}
}
