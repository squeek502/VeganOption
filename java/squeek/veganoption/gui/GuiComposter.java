package squeek.veganoption.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.CompostRegistry;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.inventory.ContainerComposter;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiComposter extends GuiContainer
{
	protected IInventory playerInventory = null;
	protected IInventory inventory = null;
	public static final ResourceLocation guiTexture = new ResourceLocation("textures/gui/container/generic_54.png");
	public int xStart;
	public int yStart;
	public int inventoryRows;

	public GuiComposter(InventoryPlayer playerInventory, TileEntityComposter composter)
	{
		super(new ContainerComposter(playerInventory, composter));
		this.inventory = composter;
		this.playerInventory = playerInventory;
		this.inventoryRows = inventory.getSizeInventory() / 9;
		this.ySize = 114 + this.inventoryRows * GuiHelper.STANDARD_SLOT_WIDTH;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.xStart = (this.width - this.xSize) / 2;
		this.yStart = (this.height - this.ySize) / 2;

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event)
	{
		if (CompostRegistry.isBrown(event.itemStack))
		{
			String itemName = event.toolTip.get(0);
			event.toolTip.set(0, EnumChatFormatting.GOLD + itemName + EnumChatFormatting.RESET);
		}
		else if (CompostRegistry.isGreen(event.itemStack))
		{
			String itemName = event.toolTip.get(0);
			event.toolTip.set(0, EnumChatFormatting.GREEN + itemName + EnumChatFormatting.RESET);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRendererObj.drawString(this.inventory.hasCustomInventoryName() ? this.inventory.getInventoryName() : I18n.format(this.inventory.getInventoryName()), 8, 6, ColorHelper.DEFAULT_TEXT_COLOR);
		this.fontRendererObj.drawString(this.playerInventory.hasCustomInventoryName() ? this.playerInventory.getInventoryName() : I18n.format(this.playerInventory.getInventoryName()), 8, this.ySize - 96 + 2, ColorHelper.DEFAULT_TEXT_COLOR);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiTexture);
		this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);

		int xStart = (this.width - this.xSize) / 2;
		int yStart = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.inventoryRows * GuiHelper.STANDARD_SLOT_WIDTH + 17);
		this.drawTexturedModalRect(xStart, yStart + this.inventoryRows * GuiHelper.STANDARD_SLOT_WIDTH + 17, 0, 126, this.xSize, 96);
	}
}
