package squeek.veganoption.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
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

import java.util.ArrayList;
import java.util.List;

public class ComposterScreen extends AbstractContainerScreen<ComposterMenu>
{
	protected Inventory playerInventory;
	public int inventoryRows;
	private StateSwitchingButton tumbleButton;
	public static final ResourceLocation TEXTURE_BG = new ResourceLocation("textures/gui/container/generic_54.png");
	public static final ResourceLocation TEXTURE_COMPONENTS = new ResourceLocation(ModInfo.MODID_LOWER, "textures/gui/composter.png");
	private static final WidgetSprites TUMBLE_BUTTON_SPRITES = new WidgetSprites(
		new ResourceLocation(ModInfo.MODID_LOWER, "composter/tumble_button"),
		new ResourceLocation(ModInfo.MODID_LOWER, "composter/tumble_button_disabled"),
		new ResourceLocation(ModInfo.MODID_LOWER, "composter/tumble_button_highlighted"));

	public static final int GUI_HEADER_SIZE = 17;
	public static final int SIDE_TAB_WIDTH = 18;
	public static final int SIDE_TAB_HEIGHT = 54;
	public static final int SIDE_TAB_Y_START = GUI_HEADER_SIZE;
	public static final int SIDE_TAB_OVERLAP = 3;

	public static final String DEGREE_SYMBOL = "\u00B0";


	public ComposterScreen(ComposterMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.playerInventory = playerInventory;
		this.inventoryRows = menu.getRowCount();
		this.imageHeight = 114 + this.inventoryRows * MiscHelper.STANDARD_SLOT_WIDTH;
	}

	@Override
	protected void init()
	{
		super.init();
		tumbleButton = new StateSwitchingButton(getTumbleX(), getTumbleY(), 13, 13, !menu.isAerating());
		tumbleButton.initTextureValues(TUMBLE_BUTTON_SPRITES);
	}

	@Override
	protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
	{
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			ItemStack itemstack = this.hoveredSlot.getItem();
			List<Component> tooltip = this.getTooltipFromContainerItem(itemstack);
			MutableComponent name = tooltip.get(0).copy();

			if (CompostRegistry.isBrown(itemstack))
				name.getSiblings().set(0, name.getSiblings().get(0).copy().withStyle(ChatFormatting.GOLD));
			else if (CompostRegistry.isGreen(itemstack))
				name.getSiblings().set(0, name.getSiblings().get(0).copy().withStyle(ChatFormatting.GREEN));

			tooltip.set(0, name);

			graphics.renderTooltip(this.font, tooltip, itemstack.getTooltipImage(), itemstack, mouseX, mouseY);
		}
	}

	public boolean isMouseOverTumbleButton(double mouseX, double mouseY)
	{
		int buttonStartX = getTumbleX();
		int buttonStartY = getTumbleY();
		return mouseX >= buttonStartX && mouseX < buttonStartX + 13 && mouseY >= buttonStartY && mouseY < buttonStartY + 13;
	}

	public boolean isMouseOverTemperature(int mouseX, int mouseY)
	{
		int mouseoverStartX = leftPos - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 4;
		int mouseoverStartY = topPos + SIDE_TAB_Y_START + 4;
		return mouseX >= mouseoverStartX && mouseX < mouseoverStartX + 13 && mouseY >= mouseoverStartY && mouseY < mouseoverStartY + 32;
	}

	public boolean isMouseOverCompostingPercent(int mouseX, int mouseY)
	{
		int mouseoverStartX = leftPos + imageWidth - SIDE_TAB_OVERLAP + 1;
		int mouseoverStartY = topPos + SIDE_TAB_Y_START + 5;
		return mouseX >= mouseoverStartX && mouseX < mouseoverStartX + 12 && mouseY >= mouseoverStartY && mouseY < mouseoverStartY + 44;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		tumbleButton.setStateTriggered(!menu.isAerating());
		tumbleButton.render(graphics, mouseX, mouseY, partialTicks);

		renderTooltip(graphics, mouseX, mouseY);

		if (isMouseOverTumbleButton(mouseX, mouseY))
		{
			graphics.renderTooltip(font, getRobustToolTip("gui.composter.tumble"), mouseX, mouseY);
		}
		else if (isMouseOverTemperature(mouseX, mouseY))
		{
			graphics.renderTooltip(font, getRobustToolTip("gui.composter.temperature", menu.getCompostTemperature() + DEGREE_SYMBOL + "C"), mouseX, mouseY);
		}
		else if (isMouseOverCompostingPercent(mouseX, mouseY))
		{
			graphics.renderTooltip(font, getRobustToolTip("gui.composter.composting", menu.getCompostingPercent() + "%"), mouseX, mouseY);
		}
	}

	public List<FormattedCharSequence> getRobustToolTip(String identifier, Object... args)
	{
		Component title = Component.empty().append(LangHelper.translate(identifier, args)).withStyle(ChatFormatting.GRAY);
		// Font#split returns an ImmutableList, we need it to be mutable.
		List<FormattedCharSequence> desc = new ArrayList<>(font.split(FormattedText.of(LangHelper.translate(identifier + ".desc").replaceAll("\\\\n", String.valueOf('\n')), Style.EMPTY.withColor(ChatFormatting.GRAY)), imageWidth));
		desc.add(0, title.getVisualOrderText());
		return desc;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		graphics.blit(TEXTURE_BG, leftPos, topPos, 0, 0, imageWidth, inventoryRows * MiscHelper.STANDARD_SLOT_WIDTH + GUI_HEADER_SIZE);
		graphics.blit(TEXTURE_BG, leftPos, topPos + inventoryRows * MiscHelper.STANDARD_SLOT_WIDTH + GUI_HEADER_SIZE, 0, 126, imageWidth, 96);
		graphics.blit(TEXTURE_COMPONENTS, leftPos - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP, topPos + SIDE_TAB_Y_START, 0, 0, SIDE_TAB_WIDTH, SIDE_TAB_HEIGHT);
		graphics.blit(TEXTURE_COMPONENTS, leftPos + imageWidth - SIDE_TAB_OVERLAP, topPos + SIDE_TAB_Y_START, SIDE_TAB_WIDTH, 0, SIDE_TAB_WIDTH, SIDE_TAB_HEIGHT);

		// composting percent
		graphics.blit(TEXTURE_COMPONENTS, leftPos + imageWidth - SIDE_TAB_OVERLAP + 1, topPos + SIDE_TAB_Y_START + 5, 47, 0, 12, Math.round((menu.getCompostingPercent() / 100f) * 44));

		// temperature
		int temperatureHeight = Math.max(0, Math.round((menu.getCompostTemperature() - menu.getBiomeTemperature()) / (TileEntityComposter.MAX_COMPOST_TEMPERATURE - menu.getBiomeTemperature()) * 30));
		graphics.blit(TEXTURE_COMPONENTS, leftPos - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 5, topPos + SIDE_TAB_Y_START + 5 + 30 - temperatureHeight, 36, 30 - temperatureHeight, 11, temperatureHeight);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (tumbleButton.mouseClicked(mouseX, mouseY, button) && !menu.isAerating())
		{
			tumbleButton.setStateTriggered(false);
			NetworkHandler.channel.sendToServer(new MessageComposterTumble(null));
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private int getTumbleX()
	{
		return leftPos - SIDE_TAB_WIDTH + SIDE_TAB_OVERLAP + 4;
	}

	private int getTumbleY()
	{
		return topPos + SIDE_TAB_Y_START + SIDE_TAB_HEIGHT - 17;
	}
}
