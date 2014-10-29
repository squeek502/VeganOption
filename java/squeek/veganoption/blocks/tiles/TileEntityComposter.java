package squeek.veganoption.blocks.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import scala.actors.threadpool.Arrays;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.InventoryHelper;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.registry.CompostRegistry;
import squeek.veganoption.registry.Content;

public class TileEntityComposter extends TileEntity implements IInventory
{
	public static final float IDEAL_GREEN_TO_BROWN_RATIO = 0.666666667f; // 2 to 1
	public static final int TICKS_PER_DAY = 24000; // 20 minutes realtime
	public static final int TICKS_BETWEEN_COMPOST_ATTEMPTS = TICKS_PER_DAY / 2;
	public static final int IDEAL_TICKS_BETWEEN_AERATIONS = TICKS_PER_DAY;
	public static final int EARLY_AERATION_WINDOW = IDEAL_TICKS_BETWEEN_AERATIONS / 6;
	public static final int ACCEPTABLE_TICKS_BETWEEN_AERATIONS = IDEAL_TICKS_BETWEEN_AERATIONS - EARLY_AERATION_WINDOW;
	public static final int UNAERATED_ROTTING_THRESHOLD = IDEAL_TICKS_BETWEEN_AERATIONS / 4;

	protected ItemStack[] inventoryItems;
	protected float compostPercent;
	protected long lastAeration;
	protected float lastAerationEffectiveness = 1f;

	public TileEntityComposter()
	{
		super();
		this.inventoryItems = new ItemStack[getSizeInventory()];
	}

	/*
	 * BlockComposter delegated methods
	 */
	public boolean onActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!player.isSneaking())
			return GuiHelper.openGuiOfTile(player, this);
		else
		{
			if (!worldObj.isRemote)
				aerate();
			return true;
		}
	}

	public void onBlockBroken()
	{
		InventoryHelper.dropAllInventoryItemsInWorld(worldObj, xCoord, yCoord, zCoord, this);
	}

	/*
	 * Composting
	 */
	@Override
	public void updateEntity()
	{
		if (lastAeration == 0)
		{
			lastAeration = worldObj.getWorldTime();
		}

		if (!worldObj.isRemote)
		{
			float compostingSpeedMultiplier = getCompostingSpeedMultiplier() * getAerationSpeedMultiplier();
			if (compostingSpeedMultiplier > 0)
			{
				compostPercent += compostingSpeedMultiplier / TICKS_BETWEEN_COMPOST_ATTEMPTS;
				if (compostPercent >= 1f)
				{
					attemptCompost();
					compostPercent = 0f;
				}
			}
		}
		super.updateEntity();
	}

	public float getCompostingPercent()
	{
		return compostPercent;
	}

	public boolean attemptCompost()
	{
		if (isRotting())
		{
			List<Integer> greenSlots = getGreenSlots();
			List<Integer> rottenPlantSlots = new ArrayList<Integer>();
			for (Integer slotNum : greenSlots)
			{
				if (getStackInSlot(slotNum).getItem() == Content.rottenPlants)
				{
					rottenPlantSlots.add(slotNum);
				}
			}
			greenSlots.removeAll(rottenPlantSlots);
			int randomGreen = greenSlots.get(RandomHelper.random.nextInt(greenSlots.size()));

			setInventorySlotContents(randomGreen, new ItemStack(Content.rottenPlants));
			return true;
		}
		else
		{
			List<Integer> greenSlots = getGreenSlots();
			List<Integer> brownSlots = getBrownSlots();

			if (greenSlots.size() >= 2 && brownSlots.size() >= 1)
			{
				int firstRandomGreen = greenSlots.get(RandomHelper.random.nextInt(greenSlots.size()));
				greenSlots.remove(firstRandomGreen);
				int secondRandomGreen = greenSlots.get(RandomHelper.random.nextInt(greenSlots.size()));
				int randomBrown = brownSlots.get(RandomHelper.random.nextInt(brownSlots.size()));

				setInventorySlotContents(firstRandomGreen, new ItemStack(Content.compost));
				setInventorySlotContents(secondRandomGreen, null);
				setInventorySlotContents(randomBrown, null);
				return true;
			}
		}
		return false;
	}

	public void aerate()
	{
		// shuffle the inventory
		@SuppressWarnings("unchecked")
		List<ItemStack> itemList = Arrays.asList(inventoryItems);
		Collections.shuffle(itemList);
		for (int slotNum = 0; slotNum < getSizeInventory(); slotNum++)
		{
			setInventorySlotContents(slotNum, itemList.get(slotNum));
		}

		long ticksSinceLastAeration = worldObj.getWorldTime() - lastAeration;
		if (ticksSinceLastAeration > ACCEPTABLE_TICKS_BETWEEN_AERATIONS)
		{
			lastAerationEffectiveness = 1f;
		}
		else
		{
			// TODO: bell curve affect, meaning that aerating over and over at one time has little affect
			float percentTooEarly = ticksSinceLastAeration / (float) ACCEPTABLE_TICKS_BETWEEN_AERATIONS;
			lastAerationEffectiveness *= 0.9f + percentTooEarly * 0.1f;
		}
		lastAeration = worldObj.getWorldTime();
	}

	public boolean isRotting()
	{
		if (getGreenAmount() <= 0)
			return false;

		if (worldObj.getWorldTime() - lastAeration >= IDEAL_TICKS_BETWEEN_AERATIONS + UNAERATED_ROTTING_THRESHOLD)
			return true;

		if (getBrownAmount() <= 0)
			return true;

		return false;
	}

	public List<Integer> getBrownSlots()
	{
		List<Integer> brownSlots = new ArrayList<Integer>();
		for (int slotNum = 0; slotNum < getSizeInventory(); slotNum++)
		{
			ItemStack itemStack = getStackInSlot(slotNum);

			if (itemStack != null && CompostRegistry.isBrown(itemStack))
			{
				brownSlots.add(slotNum);
			}
		}
		return brownSlots;
	}

	public List<Integer> getGreenSlots()
	{
		List<Integer> greenSlots = new ArrayList<Integer>();
		for (int slotNum = 0; slotNum < getSizeInventory(); slotNum++)
		{
			ItemStack itemStack = getStackInSlot(slotNum);

			if (itemStack != null && CompostRegistry.isGreen(itemStack))
			{
				greenSlots.add(slotNum);
			}
		}
		return greenSlots;
	}

	public int getBrownAmount()
	{
		return getBrownSlots().size();
	}

	public int getGreenAmount()
	{
		return getGreenSlots().size();
	}

	public float getCompostingSpeedMultiplier()
	{
		float normalizedDeltaFromIdealRatio = (getGreenToBrownRatio() - IDEAL_GREEN_TO_BROWN_RATIO) / IDEAL_GREEN_TO_BROWN_RATIO;

		// too much brown slows things down
		if (normalizedDeltaFromIdealRatio < 0)
		{
			// shift so that -1 becomes 0 and 0 becomes 1
			return 1f + normalizedDeltaFromIdealRatio;
		}
		// too much green doesn't affect the speed
		else
			return 1f;
	}

	public float getAerationSpeedMultiplier()
	{
		long ticksSinceLastAeration = worldObj.getWorldTime() - lastAeration;
		long deltaTicksFromIdeal = ticksSinceLastAeration - IDEAL_TICKS_BETWEEN_AERATIONS;
		// too little aeration is bad
		if (deltaTicksFromIdeal > 0)
		{
			// stops completely after 4 days
			return Math.max(0, 1f - deltaTicksFromIdeal / (float) (TICKS_PER_DAY * 4));
		}
		// too much aeration can also be bad
		else
		{
			if (lastAerationEffectiveness < 1f)
			{
				float recoveredPercent = ticksSinceLastAeration / (float) IDEAL_TICKS_BETWEEN_AERATIONS;
				return (float) Math.pow(lastAerationEffectiveness, 1 - recoveredPercent);
			}
			return lastAerationEffectiveness;
		}
	}

	/**
	 * @return normalized float between 0.0 and 1.0
	 * 0.0 means 100% brown and 1.0 means 100% green
	 */
	public float getGreenToBrownRatio()
	{
		int brownAmount = getBrownAmount();
		int greenAmount = getGreenAmount();
		if (brownAmount + greenAmount <= 0)
			return 0.0f;
		else if (brownAmount <= 0)
			return 1.0f;
		else
			return (float) greenAmount / (brownAmount + greenAmount);
	}

	/*
	 * Inventory utility
	 */
	protected void onSlotFilled(int slotNum)
	{
	}

	protected void onSlotEmptied(int slotNum)
	{
	}

	public void onInventoryChanged()
	{
	}

	public boolean isInventoryEmpty()
	{
		for (ItemStack itemStack : inventoryItems)
		{
			if (itemStack != null)
				return false;
		}
		return true;
	}

	public boolean isInventoryFull()
	{
		for (ItemStack itemStack : inventoryItems)
		{
			if (itemStack == null || itemStack.stackSize < Math.min(getInventoryStackLimit(), itemStack.getMaxStackSize()))
				return false;
		}
		return true;
	}

	public boolean isValidSlotNum(int slotNum)
	{
		return slotNum < getSizeInventory() && slotNum >= 0;
	}

	/*
	 * IInventory implementation
	 */
	@Override
	public int getSizeInventory()
	{
		return 27;
	}

	@Override
	public ItemStack getStackInSlot(int slotNum)
	{
		if (isValidSlotNum(slotNum))
			return inventoryItems[slotNum];
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotNum, int count)
	{
		ItemStack itemStack = getStackInSlot(slotNum);

		if (itemStack != null)
		{
			if (itemStack.stackSize <= count)
				setInventorySlotContents(slotNum, null);
			else
			{
				itemStack = itemStack.splitStack(count);
				markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotNum)
	{
		ItemStack item = getStackInSlot(slotNum);
		setInventorySlotContents(slotNum, null);
		return item;
	}

	@Override
	public void setInventorySlotContents(int slotNum, ItemStack itemStack)
	{
		if (!isValidSlotNum(slotNum))
			return;

		boolean wasEmpty = getStackInSlot(slotNum) == null;
		inventoryItems[slotNum] = itemStack;

		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
			itemStack.stackSize = getInventoryStackLimit();

		if (wasEmpty && itemStack != null)
			onSlotFilled(slotNum);
		else if (!wasEmpty && itemStack == null)
			onSlotEmptied(slotNum);

		markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return new ItemStack(Content.composter).getDisplayName();
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public void markDirty()
	{
		super.markDirty();
		onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public boolean isItemValidForSlot(int slotNum, ItemStack itemStack)
	{
		return CompostRegistry.isCompostable(itemStack) || Block.getBlockFromItem(itemStack.getItem()) == Content.compost;
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
	}

	/*
	 * Save data
	 */
	@Override
	public void writeToNBT(NBTTagCompound data)
	{
		super.writeToNBT(data);

		data.setFloat("Compost", compostPercent);
		data.setLong("LastAeration", lastAeration);
		data.setFloat("AerationEffectiveness", lastAerationEffectiveness);

		NBTTagList items = new NBTTagList();
		for (int slotNum = 0; slotNum < getSizeInventory(); slotNum++)
		{
			ItemStack stack = getStackInSlot(slotNum);

			if (stack != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) slotNum);
				stack.writeToNBT(item);
				items.appendTag(item);
			}
		}
		data.setTag("Items", items);
	}

	@Override
	public void readFromNBT(NBTTagCompound data)
	{
		super.readFromNBT(data);

		compostPercent = data.getFloat("Compost");
		lastAeration = data.getLong("LastAeration");
		lastAerationEffectiveness = data.getFloat("AerationEffectiveness");

		NBTTagList items = data.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int slotNum = 0; slotNum < items.tagCount(); slotNum++)
		{
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(slotNum);
			int slot = item.getByte("Slot");

			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}
}
