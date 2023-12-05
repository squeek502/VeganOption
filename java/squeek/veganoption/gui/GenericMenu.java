package squeek.veganoption.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.function.TriFunction;

public abstract class GenericMenu extends AbstractContainerMenu
{
	protected Container inventory;
	protected ContainerLevelAccess access;
	protected int nextSlotIndex = 0;
	protected boolean allowShiftClickToMultipleSlots = false;

	public GenericMenu(MenuType type, int containerID, ContainerLevelAccess access)
		{
			super(type, containerID);
			this.access = access;
		}

	private int getNextSlotIndex()
	{
		nextSlotIndex++;
		return nextSlotIndex - 1;
	}

	protected void addSlots(TriFunction<Integer, Integer, Integer, Slot> slotFunction, int xStart, int yStart, int rows)
	{
		addSlots(slotFunction, xStart, yStart, inventory.getContainerSize(), rows);
	}

	protected void addSlots(TriFunction<Integer, Integer, Integer, Slot> slotFunction, int xStart, int yStart, int numSlots, int rows)
	{
		int numSlotsPerRow = numSlots / rows;
		for (int i = 0, col = 0, row = 0; i < numSlots; ++i, ++col)
		{
			if (col >= numSlotsPerRow)
			{
				row++;
				col = 0;
			}

			addSlot(slotFunction.apply(getNextSlotIndex(), xStart + col * 18, yStart + row * 18));
		}
	}

	protected void addPlayerInventorySlots(Inventory playerInventory, int yStart)
	{
		addPlayerInventorySlots(playerInventory, 8, yStart);
	}

	protected void addPlayerInventorySlots(Inventory playerInventory, int xStart, int yStart)
	{
		// inventory
		for (int row = 0; row < 3; ++row)
		{
			for (int col = 0; col < 9; ++col)
			{
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, xStart + col * 18, yStart + row * 18));
			}
		}

		// hotbar
		for (int col = 0; col < 9; ++col)
		{
			this.addSlot(new Slot(playerInventory, col, xStart + col * 18, yStart + 58));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotNum)
	{
		Slot slot = this.getSlot(slotNum);

		if (slot != null && slot.hasItem())
		{
			ItemStack stackInSlot = slot.getItem();
			ItemStack stackToTransfer = stackInSlot;

			// transferring from the container to the player inventory
			if (slotNum < this.inventory.getContainerSize())
			{
				if (!this.moveItemStackTo(stackToTransfer, this.inventory.getContainerSize(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			// transferring from the player inventory into the container
			else
			{
				if (!this.moveItemStackTo(stackToTransfer, 0, this.inventory.getContainerSize(), false))
				{
					return ItemStack.EMPTY;
				}
			}

			if (stackToTransfer.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}

			// returning the remainder will attempt to fill any other valid slots with it
			if (allowShiftClickToMultipleSlots)
				return stackToTransfer;
		}

		// returning null stops it from attempting to fill consecutive slots with the remaining stack
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return AbstractContainerMenu.stillValid(access, player, getBlock());
	}

	/**
	 * @return The Block associated with this Container.
	 */
	protected abstract Block getBlock();
}
