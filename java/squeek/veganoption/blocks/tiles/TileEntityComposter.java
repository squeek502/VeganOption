package squeek.veganoption.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.NetworkHooks;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.gui.ComposterMenu;
import squeek.veganoption.helpers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileEntityComposter extends BaseContainerBlockEntity
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
	public static final int DATASLOT_ID_PERCENT_COMPOSTED = 0;
	public static final int DATASLOT_ID_BIOME_TEMPERATURE = 1;
	public static final int DATASLOT_ID_COMPOST_TEMPERATURE = 2;
	public static final int DATASLOT_ID_IS_AERATING = 3;
	public static final int NUM_DATASLOTS = 4;

	protected NonNullList<ItemStack> inventoryItems;
	protected float compostPercent;
	public long lastAeration;
	public long compostStart = NOT_COMPOSTING;
	public float compostTemperature;
	public float biomeTemperature;

	// Lid stuff taken from ChestBlockEntity.
	private ChestLidController lidController = new ChestLidController();
	private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state)
		{
			playSound(level, pos, SoundEvents.CHEST_OPEN);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state)
		{
			playSound(level, pos, SoundEvents.CHEST_CLOSE);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount)
		{
			level.blockEvent(pos, state.getBlock(), CLIENT_EVENT_NUM_USING_PLAYERS, openCount);
		}

		@Override
		protected boolean isOwnContainer(Player player)
		{
			return player.containerMenu instanceof ComposterMenu menu && menu.getContainer() == TileEntityComposter.this;
		}

		private void playSound(Level level, BlockPos pos, SoundEvent sound)
		{
			level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1F + 0.9F);
		}
	};

	public ContainerData dataAccess = new ContainerData()
	{
		@Override
		public int get(int id)
		{
			return switch (id)
			{
				case DATASLOT_ID_BIOME_TEMPERATURE -> Math.round(biomeTemperature);
				case DATASLOT_ID_PERCENT_COMPOSTED -> (int) (getCompostingPercent() * 100);
				case DATASLOT_ID_COMPOST_TEMPERATURE -> (int) getCompostTemperature();
				case DATASLOT_ID_IS_AERATING -> isAerating() ? 1 : 0;
				default -> 0;
			};
		}

		@Override
		public void set(int id, int value)
		{
			switch (id)
			{
				case DATASLOT_ID_BIOME_TEMPERATURE -> biomeTemperature = value;
				case DATASLOT_ID_PERCENT_COMPOSTED -> setCompostingPercent((float) value / 100);
				case DATASLOT_ID_COMPOST_TEMPERATURE -> setTemperature((float) value);
				case DATASLOT_ID_IS_AERATING -> aerate();
			}
		}

		@Override
		public int getCount()
		{
			return NUM_DATASLOTS;
		}
	};

	public TileEntityComposter(BlockPos pos, BlockState state)
	{
		super(Composting.composterEntityType.get(), pos, state);
		this.inventoryItems = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
	}

	/*
	 * BlockComposter delegated methods
	 */
	public boolean onActivated(Player player)
	{
		if (player.isCrouching() && !level.isClientSide() && !isAerating())
		{
			aerate();
			return true;
		}

		if (!player.isCrouching() && !level.isClientSide() && player instanceof ServerPlayer)
		{
			NetworkHooks.openScreen((ServerPlayer) player, this, getBlockPos());
			return true;
		}

		return false;
	}

	public void onBlockBroken()
	{
		Containers.dropContents(level, getBlockPos(), this);
	}

	public int getComparatorSignalStrength()
	{
		if (isComposting())
			return Math.max(0, Mth.floor(getCompostTemperature() / MAX_COMPOST_TEMPERATURE * MiscHelper.MAX_REDSTONE_SIGNAL_STRENGTH));
		else
			return 0;
	}

	@Override
	public void onLoad()
	{
		updateBiomeTemperature();
	}

	/*
	 * Composting
	 */
	public static <T extends BlockEntity> void onTick(Level level, BlockPos blockPos, BlockState blockState, T t)
	{
		if (t instanceof TileEntityComposter composter)
		{
			if (level.isClientSide())
				composter.onClientTick();
			else
				composter.onServerTick(level);
		}
	}

	private void onServerTick(Level level)
	{
		openersCounter.recheckOpeners(level, getBlockPos(), getBlockState());
		if (isComposting() && !isAerating())
		{
			long ticksSinceCycleStart = level.getGameTime() - Math.max(compostStart, lastAeration);
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

	private void onClientTick()
	{
		lidController.tickLid();
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
				ItemStack stack = getItem(slotNum);
				if (!stack.isEmpty() && stack.getItem() == Composting.rottenPlants.get())
				{
					rottenPlantSlots.add(slotNum);
				}
			}
			greenSlots.removeAll(rottenPlantSlots);
			if (greenSlots.size() > 0)
			{
				int randomGreen = greenSlots.get(RandomHelper.random.nextInt(greenSlots.size()));

				setItem(randomGreen, new ItemStack(Composting.rottenPlants.get()));
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

				setItem(firstRandomGreen, new ItemStack(Composting.compost.get()));
				setItem(secondRandomGreen, ItemStack.EMPTY);
				setItem(randomBrown, ItemStack.EMPTY);
				return true;
			}
		}
		return false;
	}

	public void aerate()
	{
		// shuffle the inventory
		Collections.shuffle(inventoryItems);
		for (int slotNum = 0; slotNum < getContainerSize(); slotNum++)
		{
			setItem(slotNum, inventoryItems.get(slotNum));
		}
		lastAeration = level.getGameTime();
		level.sendBlockUpdated(getBlockPos(), level.getBlockState(getBlockPos()), level.getBlockState(getBlockPos()), 0);
	}

	public boolean isAerating()
	{
		long ticksSinceLastAeration = level.getGameTime() - lastAeration;
		return ticksSinceLastAeration >= 0 && ticksSinceLastAeration <= NUM_TICKS_FOR_FULL_AERATION;
	}

	public float getAeratingPercent()
	{
		if (!isAerating())
			return NOT_AERATING;

		return (float) ((level.getGameTime() - lastAeration) / (double) TileEntityComposter.NUM_TICKS_FOR_FULL_AERATION);
	}

	public void setTemperature(float temperature)
	{
		float oldTemp = compostTemperature;
		compostTemperature = temperature;
		clampTemperature();
		markDirty();

		if (Math.round(compostTemperature) != Math.round(oldTemp))
		{
			level.sendBlockUpdated(getBlockPos(), level.getBlockState(getBlockPos()), level.getBlockState(getBlockPos()), 0);
		}
	}

	public void resetTemperature()
	{
		setTemperature(getBiomeTemperature());
	}

	public void clampTemperature()
	{
		float airTemperature = TemperatureHelper.getBiomeTemperature(level, getBlockPos());
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
		biomeTemperature = TemperatureHelper.getBiomeTemperature(level, getBlockPos());
	}

	/**
	 * @return float between 1.0 and -1.0, to be used as the direction/speed of temperature change
	 */
	public static float getTemperatureDeltaAtTime(float percentCooled)
	{
		return (float) (-2f / (1f + Math.exp(-percentCooled * 15f + 5f)) + 1f);
	}

	public boolean isRotting()
	{
		return getGreenAmount() > 0 && getBrownAmount() <= 0;

	}

	public List<Integer> getBrownSlots()
	{
		List<Integer> brownSlots = new ArrayList<Integer>();
		for (int slotNum = 0; slotNum < getContainerSize(); slotNum++)
		{
			ItemStack itemStack = getItem(slotNum);

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
		for (int slotNum = 0; slotNum < getContainerSize(); slotNum++)
		{
			ItemStack itemStack = getItem(slotNum);

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
		compostStart = level.getGameTime();
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
			if (itemStack == null || itemStack.getCount() < Math.min(getMaxStackSize(), itemStack.getMaxStackSize()))
				return false;
		}
		return true;
	}

	public boolean isValidSlotNum(int slotNum)
	{
		return slotNum < getContainerSize() && slotNum >= 0;
	}

	/*
	 * Container implementation
	 */
	@Override
	public int getContainerSize()
	{
		return 27;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public ItemStack getItem(int slotNum)
	{
		if (isValidSlotNum(slotNum))
			return inventoryItems.get(slotNum);
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slotNum, int count)
	{
		ItemStack itemStack = getItem(slotNum);

		if (!itemStack.isEmpty())
		{
			if (itemStack.getCount()<= count)
				setItem(slotNum, ItemStack.EMPTY);
			else
			{
				itemStack = itemStack.split(count);
				onInventoryChanged();
			}
		}

		return itemStack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slotNum)
	{
		ItemStack item = getItem(slotNum);
		setItem(slotNum, ItemStack.EMPTY);
		return item;
	}

	@Override
	public void setItem(int slotNum, ItemStack itemStack)
	{
		if (!isValidSlotNum(slotNum))
			return;

		boolean wasEmpty = getItem(slotNum).isEmpty();
		inventoryItems.set(slotNum, itemStack);

		if (!itemStack.isEmpty() && itemStack.getCount() > getMaxStackSize())
			itemStack.setCount(getMaxStackSize());

		if (wasEmpty && !itemStack.isEmpty())
			onSlotFilled(slotNum);
		else if (!wasEmpty && itemStack.isEmpty())
			onSlotEmptied(slotNum);

		onInventoryChanged();
	}

	@Override
	protected Component getDefaultName()
	{
		return Component.translatable(LangHelper.prependModId("gui.composter.name"));
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerID, Inventory inventory)
	{
		return new ComposterMenu(containerID, inventory, ContainerLevelAccess.create(getLevel(), getBlockPos()), getBlockPos(), dataAccess);
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getMaxStackSize()
	{
		return 1;
	}

	@Override
	public boolean stillValid(Player player)
	{
		BlockPos pos = getBlockPos();
		return level.getBlockEntity(pos) == this && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0D;
	}

	@Override
	public boolean canPlaceItem(int slotNum, ItemStack itemStack)
	{
		return CompostRegistry.isCompostable(itemStack);
	}

	/*
	 * Lid angle and whatnot
	 */
	@Override
	public boolean triggerEvent(int eventId, int data)
	{
		if (eventId == CLIENT_EVENT_NUM_USING_PLAYERS)
		{
			lidController.shouldBeOpen(data > 0);
			return true;
		}
		else
		{
			return super.triggerEvent(eventId, data);
		}
	}

	@Override
	public void startOpen(Player player)
	{
		if (!remove && !player.isSpectator() && level != null)
			openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState());
	}

	@Override
	public void stopOpen(Player player)
	{
		if (!remove && !player.isSpectator() && level != null)
			openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
	}

	/*
	 * Synced data
	 */
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
	{
		handleUpdateTag(pkt.getTag());
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		tag.putLong("LastAeration", lastAeration);
		tag.putFloat("Temp", compostTemperature);
		tag.putFloat("Compost", compostPercent);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag)
	{
		lastAeration = tag.getLong("LastAeration");
		compostTemperature = tag.getFloat("Temp");
		compostPercent = tag.getFloat("Compost");
	}

	private void markDirty()
	{
		setChanged();
		if (level instanceof ServerLevel serverLevel)
			serverLevel.getChunkSource().blockChanged(worldPosition);
	}

	/*
	 * Save data
	 */
	@Override
	public void saveAdditional(CompoundTag data)
	{
		super.saveAdditional(data);

		data.putFloat("Compost", compostPercent);
		data.putLong("LastAeration", lastAeration);
		data.putLong("Start", compostStart);
		data.putFloat("Temperature", compostTemperature);

		ContainerHelper.saveAllItems(data, inventoryItems);
	}

	@Override
	public void load(CompoundTag data)
	{
		super.load(data);

		compostPercent = data.getFloat("Compost");
		lastAeration = data.getLong("LastAeration");
		compostStart = data.getLong("Start");
		compostTemperature = data.getLong("Temperature");

		ContainerHelper.loadAllItems(data, inventoryItems);
	}

	@Override
	public void clearContent()
	{
		inventoryItems.clear();
	}

	public float getLidOpenness(float partialTickTime)
	{
		return lidController.getOpenness(partialTickTime);
	}
}
