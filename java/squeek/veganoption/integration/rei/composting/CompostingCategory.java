package squeek.veganoption.integration.rei.composting;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.animator.NumberAnimator;
import me.shedaniel.clothconfig2.api.animator.ValueAnimator;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.impl.client.gui.InternalTextures;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.integration.rei.VeganOptionClientPlugin;

import java.util.ArrayList;
import java.util.List;

public class CompostingCategory implements DisplayCategory<CompostingDisplay>
{
	@Override
	public List<Widget> setupDisplay(CompostingDisplay display, Rectangle bounds)
	{
		List<Widget> widgets = new ArrayList<>();
		Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);

		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)));

		// green
		List<EntryStack<?>> greenInputs = new ArrayList<>(display.getGreenIngredients());
		widgets.add(Widgets.createDrawableWidget((matrices, mouseX, mouseY, delta) -> {
			Slot slot = new ColoredSlotWidget(new Point(startPoint.x, startPoint.y + 18), ColoredSlotWidget.ColorDefinition.GREEN).markInput();
			if (display.getNumGreens() >= 1)
				slot.entries(greenInputs);
			slot.render(matrices, mouseX, mouseY, delta);
		}));
		List<EntryStack<?>> shuffledGreens = MiscHelper.newShuffledList(greenInputs);
		widgets.add(Widgets.createDrawableWidget((matrices, mouseX, mouseY, delta) -> {
			Slot slot = new ColoredSlotWidget(new Point(startPoint.x + 18, startPoint.y + 18), ColoredSlotWidget.ColorDefinition.GREEN).markInput();
			if (display.getNumGreens() == 2)
				slot.entries(shuffledGreens);
			slot.render(matrices, mouseX, mouseY, delta);
		}));

		// brown
		widgets.add(Widgets.createDrawableWidget((matrices, mouseX, mouseY, delta) -> {
			Slot slot = new ColoredSlotWidget(new Point(startPoint.x + 36, startPoint.y + 18), ColoredSlotWidget.ColorDefinition.BROWN).markInput();
			if (display.getNumBrowns() >= 1)
				slot.entries(display.getBrownIngredients());
			slot.render(matrices, mouseX, mouseY, delta);
		}));

		widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());

		return widgets;
	}

	@Override
	public CategoryIdentifier<? extends CompostingDisplay> getCategoryIdentifier()
	{
		return VeganOptionClientPlugin.Categories.COMPOSTING;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.composting"));
	}

	@Override
	public Renderer getIcon()
	{
		return EntryStacks.of(Composting.composterItem.get());
	}

	static class ColoredSlotWidget extends EntryWidget
	{
		private final ColorDefinition colorDefinition;

		ColoredSlotWidget(Point point, ColorDefinition colorDefinition)
		{
			super(point);
			this.colorDefinition = colorDefinition;
		}

		// Copied from EntryWidget
		private final NumberAnimator<Float> darkBackgroundAlpha = ValueAnimator.ofFloat()
			.withConvention(() -> REIRuntime.getInstance().isDarkThemeEnabled() ? 1.0F : 0.0F, ValueAnimator.typicalTransitionTime())
			.asFloat();

		@Override
		protected void drawBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta)
		{
			darkBackgroundAlpha.update(delta);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			RenderSystem.blendFunc(770, 771);
			// The following is the only line changed from the implementation in EntryWidget
			RenderSystem.setShaderColor(colorDefinition.r, colorDefinition.g, colorDefinition.b, 1.0F);
			graphics.blit(InternalTextures.CHEST_GUI_TEXTURE, getBounds().x, getBounds().y, 0, 222, getBounds().width, getBounds().height);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			if (darkBackgroundAlpha.value() > 0.0F) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, darkBackgroundAlpha.value());
				graphics.blit(InternalTextures.CHEST_GUI_TEXTURE_DARK, getBounds().x, getBounds().y, 0, 222, getBounds().width, getBounds().height);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		@Override
		public @Nullable Tooltip getCurrentTooltip(TooltipContext context)
		{
			Tooltip tooltip = super.getCurrentTooltip(context);
			if (tooltip != null)
			{
				Tooltip.Entry nameEntry = tooltip.entries().get(0);
				if (nameEntry != null)
				{
					Component name = nameEntry.getAsText();
					name.getSiblings().set(0, name.getSiblings().get(0).copy().withStyle(colorDefinition.tooltipColor));
					tooltip.entries().set(0, Tooltip.entry(name));
				}
			}
			return tooltip;
		}

		enum ColorDefinition
		{
			GREEN(.75f, 1f, .75f, ChatFormatting.GREEN),
			BROWN(.85f, .75f, .5f, ChatFormatting.GOLD);

			final float r, g, b;
			final ChatFormatting tooltipColor;

			ColorDefinition(float r, float g, float b, ChatFormatting tooltipColor)
			{
				this.r = r;
				this.g = g;
				this.b = b;
				this.tooltipColor = tooltipColor;
			}
		}
	}
}
