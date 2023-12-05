package squeek.veganoption.helpers;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class InventoryHelper
{
	public static float getPercentInventoryFilled(Container container)
	{
		if (container == null || container.getContainerSize() == 0)
			return 0;

		float filledPercent = 0.0F;

		for (int slotNum = 0; slotNum < container.getContainerSize(); ++slotNum)
		{
			ItemStack itemstack = container.getItem(slotNum);

			if (!itemstack.isEmpty())
				filledPercent += (float) itemstack.getCount() / (float) Math.min(container.getMaxStackSize(), itemstack.getMaxStackSize());
		}

		filledPercent /= container.getContainerSize();
		return filledPercent;
	}
}
