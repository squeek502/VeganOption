package squeek.veganoption.integration.rei.composting;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.level.ItemLike;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.integration.rei.VeganOptionClientPlugin;

import java.util.List;

public class CompostingDisplay extends BasicDisplay
{
	private final int numGreens;
	private final int numBrowns;

	public static CompostingDisplay of(EntryIngredient output, int numGreens, int numBrowns)
	{
		EntryIngredient greens = EntryIngredients.ofItems(CompostRegistry.getGreens().stream().map(item -> (ItemLike) item).toList());
		EntryIngredient browns = EntryIngredients.ofItems(CompostRegistry.getBrowns().stream().map(item -> (ItemLike) item).toList());
		return new CompostingDisplay(List.of(greens, browns), List.of(output), numGreens, numBrowns);
	}

	public CompostingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, int numGreens, int numBrowns)
	{
		super(inputs, outputs);
		this.numGreens = numGreens;
		this.numBrowns = numBrowns;
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier()
	{
		return VeganOptionClientPlugin.Categories.COMPOSTING;
	}

	public int getNumGreens()
	{
		return numGreens;
	}

	public int getNumBrowns()
	{
		return numBrowns;
	}

	public EntryIngredient getGreenIngredients()
	{
		return getInputEntries().get(0);
	}

	public EntryIngredient getBrownIngredients()
	{
		return getInputEntries().get(1);
	}
}
