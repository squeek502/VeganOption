package squeek.veganoption.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerGeneric extends Container
{
	protected IInventory inventory;
	protected EntityPlayer player;
	protected int nextSlotIndex = 0;
	protected boolean allowShiftClickToMultipleSlots = false;

	public ContainerGeneric(IInventory inventory, EntityPlayer player)
	{
		this.inventory = inventory;
		this.player = player;
		onContainerOpened(player);
	}

	protected void addSlot(IInventory inventory, int xStart, int yStart)
	{
		addSlotOfType(Slot.class, inventory, xStart, yStart);
	}

	protected void addSlots(IInventory inventory, int xStart, int yStart)
	{
		addSlotsOfType(Slot.class, inventory, xStart, yStart, 1);
	}

	protected void addSlots(IInventory inventory, int xStart, int yStart, int rows)
	{
		addSlotsOfType(Slot.class, inventory, xStart, yStart, rows);
	}

	protected void addSlots(IInventory inventory, int xStart, int yStart, int numSlots, int rows)
	{
		addSlotsOfType(Slot.class, inventory, xStart, yStart, numSlots, rows);
	}

	protected void addSlotOfType(Class<? extends Slot> slotClass, IInventory inventory, int xStart, int yStart)
	{
		addSlotsOfType(slotClass, inventory, xStart, yStart, 1, 1);
	}

	protected void addSlotsOfType(Class<? extends Slot> slotClass, IInventory inventory, int xStart, int yStart)
	{
		addSlotsOfType(slotClass, inventory, xStart, yStart, inventory.getSizeInventory(), 1);
	}

	protected void addSlotsOfType(Class<? extends Slot> slotClass, IInventory inventory, int xStart, int yStart, int rows)
	{
		addSlotsOfType(slotClass, inventory, xStart, yStart, inventory.getSizeInventory(), rows);
	}

	protected void addSlotsOfType(Class<? extends Slot> slotClass, IInventory inventory, int xStart, int yStart, int numSlots, int rows)
	{
		int numSlotsPerRow = numSlots / rows;
		for (int i = 0, col = 0, row = 0; i < numSlots; ++i, ++col)
		{
			if (col >= numSlotsPerRow)
			{
				row++;
				col = 0;
			}

			try
			{
				this.addSlotToContainer(slotClass.getConstructor(IInventory.class, int.class, int.class, int.class).newInstance(inventory, getNextSlotIndex(), xStart + col * 18, yStart + row * 18));
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected int getNextSlotIndex()
	{
		nextSlotIndex++;
		return nextSlotIndex - 1;
	}

	protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int yStart)
	{
		addPlayerInventorySlots(playerInventory, 8, yStart);
	}

	protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int xStart, int yStart)
	{
		// inventory
		for (int row = 0; row < 3; ++row)
		{
			for (int col = 0; col < 9; ++col)
			{
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, xStart + col * 18, yStart + row * 18));
			}
		}

		// hotbar
		for (int col = 0; col < 9; ++col)
		{
			this.addSlotToContainer(new Slot(playerInventory, col, xStart + col * 18, yStart + 58));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
	{
		Slot slot = this.inventorySlots.get(slotNum);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stackInSlot = slot.getStack();
			ItemStack stackToTransfer = stackInSlot;

			// transferring from the container to the player inventory
			if (slotNum < this.inventory.getSizeInventory())
			{
				if (!this.mergeItemStack(stackToTransfer, this.inventory.getSizeInventory(), this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			// transferring from the player inventory into the container
			else
			{
				if (!this.mergeItemStack(stackToTransfer, 0, this.inventory.getSizeInventory(), false))
				{
					return null;
				}
			}

			if (stackToTransfer.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}

			// returning the remainder will attempt to fill any other valid slots with it
			if (allowShiftClickToMultipleSlots)
				return stackToTransfer;
		}

		// returning null stops it from attempting to fill consecutive slots with the remaining stack
		return null;
	}

	public int getEffectiveMaxStackSizeForSlot(int slotNum, ItemStack itemStack)
	{
		int effectiveMaxStackSize = itemStack.getMaxStackSize();
		if (slotNum < inventory.getSizeInventory())
			effectiveMaxStackSize = Math.min(effectiveMaxStackSize, this.inventory.getInventoryStackLimit());
		return effectiveMaxStackSize;
	}

	@Override
	protected boolean mergeItemStack(ItemStack itemStack, int startSlotNum, int endSlotNum, boolean checkBackwards)
	{
		boolean didMerge = false;
		int k = startSlotNum;

		if (checkBackwards)
		{
			k = endSlotNum - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (itemStack.isStackable())
		{
			while (itemStack.getCount() > 0 && (!checkBackwards && k < endSlotNum || checkBackwards && k >= startSlotNum))
			{
				slot = this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!itemstack1.isEmpty() && itemstack1.getItem() == itemStack.getItem() && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, itemstack1) && slot.isItemValid(itemStack))
				{
					int l = itemstack1.getCount() + itemStack.getCount();
					int effectiveMaxStackSize = getEffectiveMaxStackSizeForSlot(k, itemStack);

					if (l <= effectiveMaxStackSize)
					{
						itemStack.setCount(0);
						itemstack1.setCount(l);
						slot.onSlotChanged();
						didMerge = true;
						break;
					}
					else if (itemstack1.getCount() < effectiveMaxStackSize)
					{
						itemStack.shrink(effectiveMaxStackSize + itemstack1.getCount());
						itemstack1.setCount(effectiveMaxStackSize);
						slot.onSlotChanged();
						didMerge = true;
						break;
					}
				}

				if (checkBackwards)
				{
					--k;
				}
				else
				{
					++k;
				}
			}
		}

		if (itemStack.getCount() > 0)
		{
			if (checkBackwards)
			{
				k = endSlotNum - 1;
			}
			else
			{
				k = startSlotNum;
			}

			while (!checkBackwards && k < endSlotNum || checkBackwards && k >= startSlotNum)
			{
				slot = this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1.isEmpty() && slot.isItemValid(itemStack))
				{
					int effectiveMaxStackSize = getEffectiveMaxStackSizeForSlot(k, itemStack);
					ItemStack transferedStack = itemStack.copy();
					if (transferedStack.getCount() > effectiveMaxStackSize)
						transferedStack.setCount(effectiveMaxStackSize);
					slot.putStack(transferedStack);
					slot.onSlotChanged();
					itemStack.shrink(transferedStack.getCount());
					didMerge = true;
					break;
				}

				if (checkBackwards)
				{
					--k;
				}
				else
				{
					++k;
				}
			}
		}

		return didMerge;
	}

	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, ClickType modifier, EntityPlayer player)
	{
		return super.slotClick(slotNum, mouseButton, modifier, player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return inventory.isUsableByPlayer(player);
	}

	public void onContainerOpened(EntityPlayer player)
	{
		inventory.openInventory(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		inventory.closeInventory(player);
		super.onContainerClosed(player);
	}
}
