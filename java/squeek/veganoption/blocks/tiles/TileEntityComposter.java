package squeek.veganoption.blocks.tiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.InventoryHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.helpers.TemperatureHelper;

public class TileEntityComposter extends TileEntity implements IInventory, ITickable
{
	public static final float IDEAL_GREEN_TO_BROWN_RATIO = 0.666666667f; // 2 to 1
	public static final int TICKS_BETWEEN_COMPOST_ATTEMPTS = MiscHelper.TICKS_PER_DAY / 2;
	public static final int TICKS_TO_FULLY_COOL = MiscHelper.TICKS_PER_DAY;
	public static final float MAX_COMPOST_TEMPERATURE = 70;
	public static final float THERMOPHILIC_RANGE_START = 45;
	public static final float MESOPHILIC_RANGE_START = 20;
	public static final int NUM_TICKS_FOR_FULL_AERATION = MiscHelper.TICKS_PER_SEC * 5;
	public static final float MAX_TEMPERATURE_DELTA_PER_TICK = 1f / (MiscHelper.TICKS_PER_SEC * 10);
	public static final float AERATION_PERCENT_TEMPERATURE_RETAINED_PER_TICK = .997f;

	public static final int CLIENT_EVENT_NUM_USING_PLAYERS = 1;

	public static final long NOT_COMPOSTING = -1;
	public static final float NOT_AERATING = -1;

	protected ItemStack[] inventoryItems;
	protected float compostPercent;
	public long lastAeration;
	public long compostStart = NOT_COMPOSTING;
	public float compostTemperature;
	public float biomeTemperature;
	protected boolean initialUpdate = true;

	// lid stuff copied from TileEntityChest
	public int numPlayersUsing = 0;
	public float lidAngle;
	public float prevLidAngle;
	public int ticksSinceSync = 0;

	public TileEntityComposter()
	{
		super();
		this.inventoryItems = new ItemStack[getSizeInventory()];
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass <= 1;
	}

	/*
	 * BlockComposter delegated methods
	 */
	public boolean onActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!player.isSneaking())
			return GuiHelper.openGuiOfTile(player, this);
		else
		{
			if (!worldObj.isRemote && !isAerating())
			{
				aerate();
			}
			return true;
		}
	}

	public void onBlockBroken()
	{
		net.minecraft.inventory.InventoryHelper.dropInventoryItems(worldObj, pos, this);
	}

	public int getComparatorSignalStrength()
	{
		if (isComposting())
			return Math.max(0, MathHelper.floor_float(getCompostTemperature() / MAX_COMPOST_TEMPERATURE * MiscHelper.MAX_REDSTONE_SIGNAL_STRENGTH));
		else
			return 0;
	}

	/*
	 * Composting
	 */
	@Override
	public void update()
	{
		if (!worldObj.isRemote)
		{
			if (initialUpdate)
			{
				updateBiomeTemperature();
			}
			if (isComposting() && !isAerating())
			{
				long ticksSinceCycleStart = worldObj.getWorldTime() - Math.max(compostStart, lastAeration);
				float percentCooled = (float) ticksSinceCycleStart / TICKS_TO_FULLY_COOL;
				float deltaTemperature = getTemperatureDeltaAtTime(percentCooled);
				deltaTemperature *= MAX_TEMPERATURE_DELTA_PER_TICK;
				if (deltaTemperature > 0)
					deltaTemperature *= getBatchTemperatureMultiplier();

				setTemperature(compostTemperature + deltaTemperature);
			}
			else if (isAerating())
			{
				setTemperature(compostTemperature * AERATION_PERCENT_TEMPERATURE_RETAINED_PER_TICK);
			}

			float compostingSpeedMultiplier = getCompostingSpeedMultiplier();
			if (compostingSpeedMultiplier > 0)
			{
				compostPercent += compostingSpeedMultiplier / TICKS_BETWEEN_COMPOST_ATTEMPTS;
				if (compostPercent >= 1f)
				{
					attemptCompost();
					compostPercent = 0f;
				}
				markDirty();
			}
		}

		updateLidAngle();

		initialUpdate = false;
	}

	public float getCompostingPercent()
	{
		return compostPercent;
	}
	
	public void setCompostingPercent(float compostPercent)
	{
		this.compostPercent = compostPercent;
	}

	public boolean attemptCompost()
	{
		if (isRotting())
		{
			List<Integer> greenSlots = getGreenSlots();
			List<Integer> rottenPlantSlots = new ArrayList<Integer>();
			for (Integer slotNum : greenSlots)
			{
				ItemStack stack = getStackInSlot(slotNum);
				if (stack != null && stack.getItem() == Composting.rottenPlants)
				{
					rottenPlantSlots.add(slotNum);
				}
			}
			greenSlots.removeAll(rottenPlantSlots);
			if (greenSlots.size() > 0)
			{
				int randomGreen = greenSlots.get(RandomHelper.random.nextInt(greenSlots.size()));

				setInventorySlotContents(randomGreen, new ItemStack(Composting.rottenPlants));
				return true;
			}
		}
		else
		{
			List<Integer> greenSlots = getGreenSlots();
			List<Integer> brownSlots = getBrownSlots();

			if (greenSlots.size() >= 2 && brownSlots.size() >= 1)
			{
				int firstRandomGreenIndex = RandomHelper.random.nextInt(greenSlots.size());
				int firstRandomGreen = greenSlots.get(firstRandomGreenIndex);
				greenSlots.remove(firstRandomGreenIndex);
				int secondRandomGreen = greenSlots.get(RandomHelper.random.nextInt(greenSlots.size()));
				int randomBrown = brownSlots.get(RandomHelper.random.nextInt(brownSlots.size()));

				setInventorySlotContents(firstRandomGreen, new ItemStack(Composting.compost));
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
		List<ItemStack> itemList = Arrays.asList(inventoryItems);
		Collections.shuffle(itemList);
		for (int slotNum = 0; slotNum < getSizeInventory(); slotNum++)
		{
			setInventorySlotContents(slotNum, itemList.get(slotNum));
		}
		lastAeration = worldObj.getWorldTime();
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 0);
	}

	public boolean isAerating()
	{
		long ticksSinceLastAeration = worldObj.getWorldTime() - lastAeration;
		return ticksSinceLastAeration >= 0 && ticksSinceLastAeration <= NUM_TICKS_FOR_FULL_AERATION;
	}

	public float getAeratingPercent()
	{
		if (!isAerating())
			return NOT_AERATING;

		return (float) ((worldObj.getWorldTime() - lastAeration) / (double) TileEntityComposter.NUM_TICKS_FOR_FULL_AERATION);
	}

	public void setTemperature(float temperature)
	{
		float oldTemp = compostTemperature;
		compostTemperature = temperature;
		clampTemperature();
		markDirty();
		
		if (Math.round(compostTemperature) != Math.round(oldTemp))
		{
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 0);
		}
	}

	public void resetTemperature()
	{
		setTemperature(getBiomeTemperature());
	}

	public void clampTemperature()
	{
		float airTemperature = TemperatureHelper.getBiomeTemperature(worldObj, pos);
		compostTemperature = Math.min(MAX_COMPOST_TEMPERATURE, Math.max(compostTemperature, airTemperature));
	}

	public float getCompostTemperature()
	{
		return compostTemperature;
	}

	public float getBiomeTemperature()
	{
		return biomeTemperature;
	}

	public void updateBiomeTemperature()
	{
		biomeTemperature = TemperatureHelper.getBiomeTemperature(worldObj, pos);
	}

	/**
	 * @return float between 1.0 and -1.0, to be used as the direction/speed of temperature change
	 */
	public static float getTemperatureDeltaAtTime(float percentCooled)
	{
		return (float) (-2f / (1f + Math.exp(-percentCooled * 15f + 5f)) + 1f);
	}

	public boolean isRotting() {
		return getGreenAmount() > 0 && getBrownAmount() <= 0;

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
		return getGreenToBrownRatioSpeedMultiplier() * getTemperatureSpeedMultiplier();
	}

	public float getGreenToBrownRatioSpeedMultiplier()
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

	public float getTemperatureSpeedMultiplier()
	{
		float temperature = getCompostTemperature();
		if (temperature >= THERMOPHILIC_RANGE_START)
		{
			float percentThermophilic = (temperature - THERMOPHILIC_RANGE_START) / (MAX_COMPOST_TEMPERATURE - THERMOPHILIC_RANGE_START);
			return 1f + percentThermophilic * 0.5f;
		}
		else if (getCompostTemperature() >= MESOPHILIC_RANGE_START)
		{
			float percentMesophilic = (temperature - MESOPHILIC_RANGE_START) / (THERMOPHILIC_RANGE_START - MESOPHILIC_RANGE_START);
			return 0.5f + percentMesophilic * 0.5f;
		}
		else
		{
			float percentToMesophilic = Math.max(0, temperature / MESOPHILIC_RANGE_START);
			return percentToMesophilic * 0.5f;
		}
	}

	public float getBatchTemperatureMultiplier()
	{
		return 0.5f + InventoryHelper.getPercentInventoryFilled(this) * 0.5f;
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

	public boolean isComposting()
	{
		return compostStart != NOT_COMPOSTING;
	}

	public void startComposting()
	{
		compostStart = worldObj.getWorldTime();
		compostPercent = 0;
	}

	public void stopComposting()
	{
		compostStart = NOT_COMPOSTING;
		compostPercent = 0;
	}

	/*
	 * Inventory utility
	 */
	protected void onSlotFilled(int slotNum)
	{
		if (!isComposting())
			startComposting();
	}

	protected void onSlotEmptied(int slotNum)
	{
		if (isInventoryEmpty())
		{
			stopComposting();
			resetTemperature();
		}
	}

	public void onInventoryChanged()
	{
		markDirty();
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
				onInventoryChanged();
			}
		}

		return itemStack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slotNum)
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

		onInventoryChanged();
	}

	@Override
	public String getName()
	{
		return new ItemStack(Composting.composter).getDisplayName();
	}

	@Override
	public boolean hasCustomName()
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
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0D;
	}

	@Override
	public boolean isItemValidForSlot(int slotNum, ItemStack itemStack)
	{
		return CompostRegistry.isCompostable(itemStack) || Block.getBlockFromItem(itemStack.getItem()) == Composting.compost;
	}

	/*
	 * Lid angle and whatnot
	 */
	@Override
	public boolean receiveClientEvent(int eventId, int data)
	{
		if (eventId == CLIENT_EVENT_NUM_USING_PLAYERS)
		{
			this.numPlayersUsing = data;
			return true;
		}
		else
		{
			return super.receiveClientEvent(eventId, data);
		}
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		if (this.numPlayersUsing < 0)
		{
			this.numPlayersUsing = 0;
		}
		++this.numPlayersUsing;

		this.worldObj.addBlockEvent(pos, this.getBlockType(), CLIENT_EVENT_NUM_USING_PLAYERS, this.numPlayersUsing);
		this.worldObj.notifyNeighborsOfStateChange(pos, this.getBlockType());
		this.worldObj.notifyNeighborsOfStateChange(pos, this.getBlockType());
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		--this.numPlayersUsing;
		if (this.numPlayersUsing < 0)
		{
			this.numPlayersUsing = 0;
		}

		this.worldObj.addBlockEvent(pos, this.getBlockType(), CLIENT_EVENT_NUM_USING_PLAYERS, this.numPlayersUsing);
		this.worldObj.notifyNeighborsOfStateChange(pos, this.getBlockType());
		this.worldObj.notifyNeighborsOfStateChange(pos.down(), this.getBlockType());
	}

	public void updateLidAngle()
	{
		++this.ticksSinceSync;
		float f;

		if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
		{
			this.numPlayersUsing = 0;
			f = 5.0F;
			List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, pos.getX() + 1 + f, pos.getY() + 1 + f, pos.getZ() + 1 + f));

			for (EntityPlayer entityplayer : list) {
				if (entityplayer.openContainer instanceof ContainerChest) {
					IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this)) {
						++this.numPlayersUsing;
					}
				}
			}
		}

		this.prevLidAngle = this.lidAngle;
		f = 0.1F;
		double d2;

		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
		{
			double d1 = pos.getX() + 0.5D;
			d2 = pos.getZ() + 0.5D;

			worldObj.playSound(d1, pos.getY() + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F, true);
		}

		if (this.numPlayersUsing <= 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
		{
			float f1 = this.lidAngle;

			if (this.numPlayersUsing > 0)
			{
				this.lidAngle += f;
			}
			else
			{
				this.lidAngle -= f;
			}

			if (this.lidAngle > 1.0F)
			{
				this.lidAngle = 1.0F;
			}

			float f2 = 0.5F;

			if (this.lidAngle < f2 && f1 >= f2)
			{
				d2 = pos.getX() + 0.5D;
				double d0 = pos.getZ() + 0.5D;

				worldObj.playSound(d2, pos.getY() + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F, true);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}

	/*
	 * Synced data
	 */
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		NBTTagCompound compound = pkt.getNbtCompound();
		lastAeration = compound.getLong("LastAeration");
		compostTemperature = compound.getFloat("Temp");
		super.onDataPacket(net, pkt);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setLong("LastAeration", lastAeration);
		compound.setFloat("Temp", compostTemperature);
		return new SPacketUpdateTileEntity(pos, 1, compound);
	}

	/*
	 * Save data
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data)
	{
		super.writeToNBT(data);

		data.setFloat("Compost", compostPercent);
		data.setLong("LastAeration", lastAeration);
		data.setLong("Start", compostStart);
		data.setFloat("Temperature", compostTemperature);

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

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data)
	{
		super.readFromNBT(data);

		compostPercent = data.getFloat("Compost");
		lastAeration = data.getLong("LastAeration");
		compostStart = data.getLong("Start");
		compostTemperature = data.getLong("Temperature");

		NBTTagList items = data.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int slotNum = 0; slotNum < items.tagCount(); slotNum++)
		{
			NBTTagCompound item = items.getCompoundTagAt(slotNum);
			int slot = item.getByte("Slot");

			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < inventoryItems.length; i++)
		{
			inventoryItems[i] = null;
		}
	}

	/*
	These are horrible, and I don't think they actually get used.
	 */

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int val)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}
}
