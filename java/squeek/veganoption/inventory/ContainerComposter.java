package squeek.veganoption.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import squeek.veganoption.blocks.tiles.TileEntityComposter;

public class ContainerComposter extends ContainerGeneric
{
	public TileEntityComposter composter;
	public int slotsX;
	public int slotsY;

	public ContainerComposter(InventoryPlayer playerInventory, TileEntityComposter composter)
	{
		super(composter);
		this.composter = composter;

		allowShiftClickToMultipleSlots = true;
		slotsX = 8;
		slotsY = 18;

		this.addSlotsOfType(SlotFiltered.class, composter, slotsX, slotsY, 3);
		this.addPlayerInventorySlots(playerInventory, 85);
	}

}
