package squeek.veganoption.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryHelper
{
	public static float getPercentInventoryFilled(IInventory inventory)
	{
		if (inventory == null || inventory.getSizeInventory() == 0)
			return 0;

		float filledPercent = 0.0F;

		for (int slotNum = 0; slotNum < inventory.getSizeInventory(); ++slotNum)
		{
			ItemStack itemstack = inventory.getStackInSlot(slotNum);

			if (!itemstack.isEmpty())
			{
				filledPercent += (float) itemstack.getCount() / (float) Math.min(inventory.getInventoryStackLimit(), itemstack.getMaxStackSize());
			}
		}

		filledPercent /= inventory.getSizeInventory();
		return filledPercent;
	}
}
