package squeek.veganoption.gui;

import invtweaks.api.container.ChestContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.modules.Composting;

@ChestContainer
public class ComposterMenu extends GenericMenu
{
	public TileEntityComposter composter;
	public int slotsX;
	public int slotsY;

	// client side constructor
	public ComposterMenu(int containerID, Inventory playerInv, BlockPos pos)
	{
		this(containerID, playerInv, ContainerLevelAccess.NULL, pos);
	}

	public ComposterMenu(int containerID, Inventory playerInv, ContainerLevelAccess access, BlockPos pos)
	{
		super(Composting.composterMenuType.get(), containerID, access);

		composter = (TileEntityComposter) playerInv.player.level().getBlockEntity(pos);
		allowShiftClickToMultipleSlots = true;
		slotsX = 8;
		slotsY = 18;

		addSlots((index, x, y) -> new SlotFiltered(composter, index, x, y), slotsX, slotsY, 3);
		addPlayerInventorySlots(playerInv, 85);
		addDataSlots(composter.dataAccess);
	}

	@Override
	protected Block getBlock()
	{
		return Composting.composter.get();
	}

	public float getBiomeTemperature()
	{
		// todo: if this works, remove data slots. if it doesn't, use data slots.
		return this.composter.getBiomeTemperature();
	}

	public float getCompostingPercent()
	{
		// todo: if this works, remove data slots. if it doesn't, use data slots.
		return this.composter.getCompostingPercent();
	}

	public float getCompostTemperature()
	{
		// todo: if this works, remove data slots. if it doesn't, use data slots.
		return this.composter.getCompostTemperature();
	}

	public boolean isAerating()
	{
		// todo: if this works, remove data slots. if it doesn't, use data slots.
		return this.composter.isAerating();
	}
}
