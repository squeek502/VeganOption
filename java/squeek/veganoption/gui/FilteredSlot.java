package squeek.veganoption.gui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Simple extension of the Slot class which only allows ItemStacks which the Container allows.
 */
public class FilteredSlot extends Slot
{
	public FilteredSlot(Container container, int id, int x, int y)
	{
		super(container, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack itemStack)
	{
		return container.canPlaceItem(getSlotIndex(), itemStack);
	}
}