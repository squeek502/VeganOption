package squeek.veganoption.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFiltered extends Slot
{
	public SlotFiltered(IInventory inventory, int id, int x, int y)
	{
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack)
	{
		return inventory.isItemValidForSlot(this.getSlotIndex(), itemStack);
	}
}