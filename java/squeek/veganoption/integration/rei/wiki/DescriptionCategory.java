package squeek.veganoption.integration.rei.wiki;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import squeek.veganoption.content.modules.CreativeTabProxy;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class DescriptionCategory<T extends DescriptionDisplay> implements DisplayCategory<T>
{
	@Override
	public List<Widget> setupDisplay(T display, Rectangle bounds)
	{
		List<Widget> widgets = new ArrayList<>();
		Font font = Minecraft.getInstance().font;

		int yPos = bounds.getY() + DescriptionMaker.PADDING;

		widgets.add(Widgets.createSlot(new Point(bounds.getCenterX() - (MiscHelper.STANDARD_SLOT_WIDTH / 2), yPos)).disableBackground().entries(EntryIngredients.of(display.getTopic())));
		yPos += MiscHelper.STANDARD_SLOT_HEIGHT;

		List<ItemStack> relatedItems = display.getRelated();
		if (display.isFirstPage() && !relatedItems.isEmpty())
		{
			yPos = addSlotsSubsection(getRelatedTitle(), bounds, font, widgets, relatedItems.stream().map(EntryIngredients::of).toList(), yPos);
		}

		int finalY = yPos;
		List<FormattedCharSequence> lines = ImmutableList.copyOf(display.getText());
		widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
			int y = finalY;
			for (FormattedCharSequence line : lines)
			{
				if (line != null)
				{
					int x = bounds.getCenterX() - (font.width(line) / 2);
					graphics.drawString(font, line, x, y, ColorHelper.DEFAULT_TEXT_COLOR, false);
					y += font.lineHeight;
				}
			}
		}));
		yPos += font.lineHeight * lines.size();

		List<EntryIngredient> referenced = display.getReferenced();
		if (display.isFirstPage() && !referenced.isEmpty())
		{
			addSlotsSubsection(Component.translatable(LangHelper.prependModId("jei.references")), bounds, font, widgets, referenced, yPos);
		}

		return widgets;
	}

	private int addSlotsSubsection(Component title, Rectangle bounds, Font font, List<Widget> widgets, List<EntryIngredient> ingredients, int yPos)
	{
		int xPos = bounds.getCenterX();

		widgets.add(Widgets.createLabel(new Point(xPos, yPos), title).color(ColorHelper.DEFAULT_TEXT_COLOR).noShadow());

		int itemSlotsWidth = ingredients.size() * MiscHelper.STANDARD_SLOT_WIDTH;
		xPos = bounds.getCenterX() - (itemSlotsWidth / 2);
		yPos += font.lineHeight + 1;

		for (EntryIngredient ingredient : ingredients)
		{
			widgets.add(Widgets.createSlot(new Point(xPos, yPos)).entries(ingredient));
			xPos += MiscHelper.STANDARD_SLOT_WIDTH;
		}

		yPos += font.lineHeight * DescriptionMaker.DESC_DISPLACEMENT - 4;

		return yPos;
	}

	@Override
	public Renderer getIcon()
	{
		return EntryStacks.of(CreativeTabProxy.proxyItem.get());
	}

	@Override
	public int getDisplayHeight()
	{
		return DescriptionMaker.HEIGHT;
	}

	@Override
	public int getFixedDisplaysPerPage()
	{
		return 1;
	}

	protected abstract Component getRelatedTitle();
}
