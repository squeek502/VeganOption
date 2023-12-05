package squeek.veganoption.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.network.MessageComposterTumble;
import squeek.veganoption.network.NetworkHandler;

import java.util.List;

public class ComposterScreen extends AbstractContainerScreen<ComposterMenu>
{
	protected Inventory inventory = null;
	public static final ResourceLocation TEXTURE_BG = new ResourceLocation("textures/gui/container/generic_54.png");
	public static final ResourceLocation TEXTURE_COMPONENTS = new ResourceLocation(ModInfo.MODID_LOWER, "textures/gui/composter.png");
	public int xStart;
	public int yStart;
	public int inventoryRows;

	public static final int GUI_HEADER_SIZE = 17;
	public static final int SIDE_TAB_WIDTH = 18;
	public static final int SIDE_TAB_HEIGHT = 54;
	public static final int SIDE_TAB_Y_START = GUI_HEADER_SIZE;
	public static final int SIDE_TAB_OVERLAP = 3;

	public static final String DEGREE_SYMBOL = "\u00B0";


	public ComposterScreen(ComposterMenu menu, Inventory inventory, Component title)
	{
		super(menu, inventory, title);
		this.inventory = inventory;
		this.inventoryRows = inventory.getContainerSize() / 9;
		this.imageHeight = 114 + this.inventoryRows * MiscHelper.STANDARD_SLOT_WIDTH;

		this.xStart = (this.width - this.imageWidth) / 2;
		this.yStart = (this.height - this.imageHeight) / 2;
	}

	@Override
	protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
	{
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			ItemStack itemstack = this.hoveredSlot.getItem();
			List<Component> tooltip = this.getTooltipFromContainerItem(itemstack);
			MutableComponent name = Component.empty().append(itemstack.getHoverName());

			if (CompostRegistry.isBrown(itemstack.getItem()))
				name.withStyle(ChatFormatting.GOLD);
			else if (CompostRegistry.isGreen(itemstack.getItem()))
				name.withStyle(ChatFormatting.GREEN);

			if (itemstack.hasCustomHoverName()) {
				name.withStyle(ChatFormatting.ITALIC);
			}

			tooltip.set(0, name);

			graphics.renderTooltip(this.font, tooltip, itemstack.getTooltipImage(), itemstack, mouseX, mouseY);
		}
	}

	public boolean isMouseOverTumbleButton(double mouseX, double mouseY)
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
		int mouseoverStartX = xStart + imageWidth - SIDE_TAB_OVERLAP + 1;
		int mouseoverStartY = yStart + SIDE_TAB_Y_START + 5;
		return mouseX >= mouseoverStartX && mouseX < mouseoverStartX + 12 && mouseY >= mouseoverStartY && mouseY < mouseoverStartY + 44;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		// tumble button
		graphics.blit(TEXTURE_COMPONENTS, -SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 4, SIDE_TAB_Y_START + SIDE_TAB_HEIGHT - 17, 0, menu.isAerating() ? 80 : (isMouseOverTumbleButton(mouseX, mouseY) ? 67 : 54), 13, 13);

		super.render(graphics, mouseX, mouseY, partialTicks);

		if (isMouseOverTumbleButton(mouseX, mouseY))
		{
			graphics.renderTooltip(font, getRobustToolTip("gui.composter.tumble"), mouseX, mouseY);
		}
		else if (isMouseOverTemperature(mouseX, mouseY))
		{
			graphics.renderTooltip(font, getRobustToolTip("gui.composter.temperature", Math.round(menu.getCompostTemperature()) + DEGREE_SYMBOL + "C"), mouseX, mouseY);
		}
		else if (isMouseOverCompostingPercent(mouseX, mouseY))
		{
			graphics.renderTooltip(font, getRobustToolTip("gui.composter.composting", ((int) (menu.getCompostingPercent() * 100)) + "%"), mouseX, mouseY);
		}
	}

	public List<FormattedCharSequence> getRobustToolTip(String identifier, Object... args)
	{
		@SuppressWarnings("unchecked")
		Component title = Component.empty().append(LangHelper.translate(identifier, args)).withStyle(ChatFormatting.GRAY);
		List<FormattedCharSequence> desc = font.split(FormattedText.of(LangHelper.translate(identifier + ".desc").replaceAll("\\\\n", String.valueOf('\n')), Style.EMPTY.withColor(ChatFormatting.GRAY)), imageWidth);
		desc.add(0, title.getVisualOrderText());
		return desc;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		graphics.blit(TEXTURE_BG, xStart, yStart, 0, 0, imageWidth, imageHeight);
		graphics.blit(TEXTURE_COMPONENTS, xStart - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP, yStart + SIDE_TAB_Y_START, 0, 0, SIDE_TAB_WIDTH, SIDE_TAB_HEIGHT);
		graphics.blit(TEXTURE_COMPONENTS, xStart + imageWidth - SIDE_TAB_OVERLAP, yStart + SIDE_TAB_Y_START, SIDE_TAB_WIDTH, 0, SIDE_TAB_WIDTH, SIDE_TAB_HEIGHT);

		// composting percent
		graphics.blit(TEXTURE_COMPONENTS, xStart + imageWidth - SIDE_TAB_OVERLAP + 1, yStart + SIDE_TAB_Y_START + 5, 47, 0, 12, Math.round(menu.getCompostingPercent() * 44));

		// temperature
		int temperatureHeight = Math.max(0, Math.round((menu.getCompostTemperature() - menu.getBiomeTemperature()) / (TileEntityComposter.MAX_COMPOST_TEMPERATURE - menu.getBiomeTemperature()) * 30));
		graphics.blit(TEXTURE_COMPONENTS, xStart - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 5, yStart + SIDE_TAB_Y_START + 5 + 30 - temperatureHeight, 36, 30 - temperatureHeight, 11, temperatureHeight);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (isMouseOverTumbleButton(mouseX, mouseY) && button == 0)
		{
			NetworkHandler.channel.sendToServer(new MessageComposterTumble(null));
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
