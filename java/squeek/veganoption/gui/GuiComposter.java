package squeek.veganoption.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.inventory.ContainerComposter;
import squeek.veganoption.network.MessageComposterTumble;
import squeek.veganoption.network.NetworkHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiComposter extends GuiContainer
{
	protected IInventory playerInventory = null;
	protected IInventory inventory = null;
	protected TileEntityComposter composter = null;
	public static final ResourceLocation guiTexture = new ResourceLocation("textures/gui/container/generic_54.png");
	public static final ResourceLocation guiComponents = new ResourceLocation(ModInfo.MODID_LOWER, "textures/gui/composter.png");
	public int xStart;
	public int yStart;
	public int inventoryRows;

	public static final int GUI_HEADER_SIZE = 17;
	public static final int SIDE_TAB_WIDTH = 18;
	public static final int SIDE_TAB_HEIGHT = 54;
	public static final int SIDE_TAB_Y_START = GUI_HEADER_SIZE;
	public static final int SIDE_TAB_OVERLAP = 3;

	public static final String DEGREE_SYMBOL = "\u00B0";

	public GuiComposter(InventoryPlayer playerInventory, TileEntityComposter composter)
	{
		super(new ContainerComposter(playerInventory, composter));
		this.composter = composter;
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
		if (CompostRegistry.isBrown(event.getItemStack()))
		{
			String itemName = event.getToolTip().get(0);
			event.getToolTip().set(0, TextFormatting.GOLD + itemName + TextFormatting.RESET);
		}
		else if (CompostRegistry.isGreen(event.getItemStack()))
		{
			String itemName = event.getToolTip().get(0);
			event.getToolTip().set(0, TextFormatting.GREEN + itemName + TextFormatting.RESET);
		}

	}

	public boolean isMouseOverTumbleButton(int mouseX, int mouseY)
	{
		int buttonStartX = xStart - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 4;
		int buttonStartY = yStart + SIDE_TAB_Y_START + SIDE_TAB_HEIGHT - 17;
		return mouseX >= buttonStartX && mouseX < buttonStartX + 13 && mouseY >= buttonStartY && mouseY < buttonStartY + 13;
	}

	public boolean isMouseOverTemperature(int mouseX, int mouseY)
	{
		int mouseoverStartX = xStart - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 4;
		int mouseoverStartY = yStart + SIDE_TAB_Y_START + 4;
		return mouseX >= mouseoverStartX && mouseX < mouseoverStartX + 13 && mouseY >= mouseoverStartY && mouseY < mouseoverStartY + 32;
	}

	public boolean isMouseOverCompostingPercent(int mouseX, int mouseY)
	{
		int mouseoverStartX = xStart + xSize - SIDE_TAB_OVERLAP + 1;
		int mouseoverStartY = yStart + SIDE_TAB_Y_START + 5;
		return mouseX >= mouseoverStartX && mouseX < mouseoverStartX + 12 && mouseY >= mouseoverStartY && mouseY < mouseoverStartY + 44;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(this.inventory.hasCustomName() ? this.inventory.getName() : I18n.format(this.inventory.getName()), 8, 6, ColorHelper.DEFAULT_TEXT_COLOR);
		this.fontRenderer.drawString(this.playerInventory.hasCustomName() ? this.playerInventory.getName() : I18n.format(this.playerInventory.getName()), 8, this.ySize - 96 + 2, ColorHelper.DEFAULT_TEXT_COLOR);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiComponents);

		// tumble button
		this.drawTexturedModalRect(-SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 4, SIDE_TAB_Y_START + SIDE_TAB_HEIGHT - 17, 0, composter.isAerating() ? 80 : (isMouseOverTumbleButton(mouseX, mouseY) ? 67 : 54), 13, 13);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	public List<String> getRobustToolTip(String identifier, Object... args)
	{
		@SuppressWarnings("unchecked")
		List<String> toolTipText = new ArrayList<String>(fontRenderer.listFormattedStringToWidth(LangHelper.translate(identifier + ".desc").replaceAll("\\\\n", String.valueOf('\n')), xSize));
		toolTipText.add(0, LangHelper.translate(identifier, args));
		for (int i = 1; i < toolTipText.size(); ++i)
		{
			toolTipText.set(i, TextFormatting.GRAY + toolTipText.get(i));
		}
		return toolTipText;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f)
	{
		super.drawScreen(mouseX, mouseY, f);

		if (isMouseOverTumbleButton(mouseX, mouseY))
		{
			drawHoveringText(getRobustToolTip("gui.composter.tumble"), mouseX, mouseY, fontRenderer);
		}
		else if (isMouseOverTemperature(mouseX, mouseY))
		{
			drawHoveringText(getRobustToolTip("gui.composter.temperature", Math.round(composter.getCompostTemperature()) + DEGREE_SYMBOL + "C"), mouseX, mouseY, fontRenderer);
		}
		else if (isMouseOverCompostingPercent(mouseX, mouseY))
		{
			drawHoveringText(getRobustToolTip("gui.composter.composting", ((int) (composter.getCompostingPercent() * 100)) + "%"), mouseX, mouseY, fontRenderer);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiTexture);

		this.drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.inventoryRows * GuiHelper.STANDARD_SLOT_WIDTH + GUI_HEADER_SIZE);
		this.drawTexturedModalRect(xStart, yStart + this.inventoryRows * GuiHelper.STANDARD_SLOT_WIDTH + GUI_HEADER_SIZE, 0, 126, this.xSize, 96);

		this.mc.getTextureManager().bindTexture(guiComponents);
		this.drawTexturedModalRect(xStart - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP, yStart + SIDE_TAB_Y_START, 0, 0, SIDE_TAB_WIDTH, SIDE_TAB_HEIGHT);
		this.drawTexturedModalRect(xStart + xSize - SIDE_TAB_OVERLAP, yStart + SIDE_TAB_Y_START, SIDE_TAB_WIDTH, 0, SIDE_TAB_WIDTH, SIDE_TAB_HEIGHT);

		// composting percent
		this.drawTexturedModalRect(xStart + xSize - SIDE_TAB_OVERLAP + 1, yStart + SIDE_TAB_Y_START + 5, 47, 0, 12, Math.round(composter.getCompostingPercent() * 44));

		// temperature
		int temperatureHeight = Math.max(0, Math.round((composter.getCompostTemperature() - composter.biomeTemperature) / (TileEntityComposter.MAX_COMPOST_TEMPERATURE - composter.biomeTemperature) * 30));
		this.drawTexturedModalRect(xStart - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 5, yStart + SIDE_TAB_Y_START + 5 + 30 - temperatureHeight, 36, 30 - temperatureHeight, 11, temperatureHeight);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int type)
	{
		if (isMouseOverTumbleButton(mouseX, mouseY) && type == 0)
		{
			NetworkHandler.channel.sendToServer(new MessageComposterTumble());
		}
		try
		{
			super.mouseClicked(mouseX, mouseY, type);
		}
		catch (IOException ignore)
		{
		}
	}
}
