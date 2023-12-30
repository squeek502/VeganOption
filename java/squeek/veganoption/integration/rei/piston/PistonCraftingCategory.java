package squeek.veganoption.integration.rei.piston;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.rei.VeganOptionClientPlugin;

import java.util.ArrayList;
import java.util.List;

public class PistonCraftingCategory implements DisplayCategory<PistonCraftingDisplay>
{
	@Override
	public List<Widget> setupDisplay(PistonCraftingDisplay display, Rectangle bounds)
	{
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));

		int startX = bounds.getCenterX() - 60;

		{
			List<List<InputIngredient<EntryStack<?>>>> inputs = Lists.partition(display.getInputIngredients(null, null), 2);
			Point inputStartPoint = new Point(startX + 36, bounds.getCenterY() - (Math.max(1, inputs.size()) * 9));
			int y = 0;
			int x;
			for (List<InputIngredient<EntryStack<?>>> row : inputs)
			{
				x = row.size();
				for (InputIngredient<EntryStack<?>> col : row)
				{
					for (EntryStack<?> s : col.get())
					{
						if (s.getType() == VanillaEntryTypes.FLUID)
							s.tooltip(Component.translatable(LangHelper.prependModId("jei.piston_crafting.tooltip.fluid_input")).withStyle(ChatFormatting.GRAY));
					}
					widgets.add(Widgets.createSlot(new Point(inputStartPoint.getX() - x * 18, inputStartPoint.getY() + 1 + y * 18))
						.markInput()
						.entries(col.get()));
					x--;
				}
				y++;
			}
		}

		{
			List<List<EntryIngredient>> outputs = Lists.partition(display.getOutputEntries(), 2);
			Point outputStartPoint = new Point(startX, bounds.getCenterY() - (Math.max(1, outputs.size()) * 9));
			int y = 0;
			int x;
			for (List<EntryIngredient> row : outputs)
			{
				x = 0;
				for (EntryIngredient col : row)
				{
					for (EntryStack<?> s : col)
					{
						if (s.getType() == VanillaEntryTypes.FLUID)
							s.tooltip(Component.translatable(LangHelper.prependModId("jei.piston_crafting.tooltip.fluid_output")).withStyle(ChatFormatting.GRAY));
					}
					widgets.add(Widgets.createSlot(new Point(outputStartPoint.getX() + 84 + x * 18, outputStartPoint.getY() + 1 + y * 18))
						.markOutput()
						.entries(col));
					x++;
				}
				y++;
			}
		}

		widgets.add(Widgets.createArrow(new Point(bounds.getCenterX() - 13, bounds.getCenterY() - 9)));

		return widgets;
	}

	@Override
	public CategoryIdentifier<PistonCraftingDisplay> getCategoryIdentifier()
	{
		return VeganOptionClientPlugin.Categories.PISTON_CRAFTING;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.piston_crafting"));
	}

	@Override
	public Renderer getIcon()
	{
		return EntryStacks.of(Items.PISTON);
	}
}
