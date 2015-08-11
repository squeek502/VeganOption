package squeek.veganoption.inventory;

import invtweaks.api.container.ChestContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ChestContainer
public class ContainerComposter extends ContainerGeneric
{
	public TileEntityComposter composter;
	public int slotsX;
	public int slotsY;
	private boolean initialUpdate = true;
	public byte lastPercentComposted = 0;
	public byte lastBiomeTemperature = 0;
	public long lastAeration = 0;

	public static final int PROGRESS_ID_PERCENT_COMPOSTED = 1;
	public static final int PROGRESS_ID_BIOME_TEMPERATURE = 2;

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

	@Override
	public void detectAndSendChanges()
	{
		// by default, isChangingQuantityOnly is set to true when calling detectAndSendChanges
		// but when the composter is aerated, the entire inventory gets shuffled, so setting
		// isChangingQuantityOnly to false before calling the default container detectAndSendChanges
		// will actually make the changes get sent
		if (initialUpdate)
			lastAeration = composter.lastAeration;
		else if (lastAeration != composter.lastAeration)
		{
			for (Object crafter : crafters)
			{
				if (crafter instanceof EntityPlayerMP)
					((EntityPlayerMP) crafter).isChangingQuantityOnly = false;
			}
			lastAeration = composter.lastAeration;
		}

		super.detectAndSendChanges();

		if ((byte) (composter.getCompostingPercent() * 100) != lastPercentComposted || initialUpdate)
		{
			for (Object crafter : crafters)
			{
				((ICrafting) crafter).sendProgressBarUpdate(this, PROGRESS_ID_PERCENT_COMPOSTED, (byte) (composter.getCompostingPercent() * 100));
			}
			lastPercentComposted = (byte) (composter.getCompostingPercent() * 100);
		}

		if (Math.round(composter.getBiomeTemperature()) != lastBiomeTemperature || initialUpdate)
		{
			for (Object crafter : crafters)
			{
				((ICrafting) crafter).sendProgressBarUpdate(this, PROGRESS_ID_BIOME_TEMPERATURE, Math.round(composter.getBiomeTemperature()));
			}
			lastBiomeTemperature = (byte) Math.round(composter.getBiomeTemperature());
		}

		initialUpdate = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value)
	{
		super.updateProgressBar(id, value);

		switch (id)
		{
			case PROGRESS_ID_PERCENT_COMPOSTED:
				composter.setCompostingPercent(value / 100.0f);
				break;
			case PROGRESS_ID_BIOME_TEMPERATURE:
				composter.biomeTemperature = value;
				break;
			default:
				throw new RuntimeException("Unexpected composter progress bar id: " + id);
		}
	}

}
