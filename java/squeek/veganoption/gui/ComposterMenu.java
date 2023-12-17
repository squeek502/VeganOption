package squeek.veganoption.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.modules.Composting;

public class ComposterMenu extends GenericMenu
{
	public TileEntityComposter composter;
	public int slotsX;
	public int slotsY;
	private final ContainerData data;

	// client side constructor
	public ComposterMenu(int containerID, Inventory playerInv, BlockPos pos)
	{
		this(containerID, playerInv, ContainerLevelAccess.NULL, pos, new SimpleContainerData(TileEntityComposter.NUM_DATASLOTS));
	}

	public ComposterMenu(int containerID, Inventory playerInv, ContainerLevelAccess access, BlockPos pos, ContainerData data)
	{
		super(Composting.composterMenuType.get(), containerID, access);
		checkContainerDataCount(data, TileEntityComposter.NUM_DATASLOTS);

		composter = (TileEntityComposter) playerInv.player.level().getBlockEntity(pos);
		allowShiftClickToMultipleSlots = true;
		slotsX = 8;
		slotsY = 18;
		this.data = data;

		composter.startOpen(playerInv.player);

		addSlots((index, x, y) -> new SlotFiltered(composter, index, x, y), slotsX, slotsY, 3);
		addPlayerInventorySlots(playerInv, 85);
		addDataSlots(data);
	}

	@Override
	public void removed(Player player)
	{
		super.removed(player);
		composter.stopOpen(player);
	}

	@Override
	protected Block getBlock()
	{
		return Composting.composter.get();
	}

	@Override
	public Container getContainer()
	{
		return composter;
	}

	public int getRowCount()
	{
		return composter.getContainerSize() / 9;
	}

	public int getBiomeTemperature()
	{
		return getData(TileEntityComposter.DATASLOT_ID_BIOME_TEMPERATURE);
	}

	public int getCompostingPercent()
	{
		return getData(TileEntityComposter.DATASLOT_ID_PERCENT_COMPOSTED);
	}

	public int getCompostTemperature()
	{
		return getData(TileEntityComposter.DATASLOT_ID_COMPOST_TEMPERATURE);
	}

	public boolean isAerating()
	{
		return getData(TileEntityComposter.DATASLOT_ID_IS_AERATING) == 1;
	}

	private int getData(int slot)
	{
		return composter.dataAccess.get(slot);
	}
}
