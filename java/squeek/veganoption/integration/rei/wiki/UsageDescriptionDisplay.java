package squeek.veganoption.integration.rei.wiki;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import squeek.veganoption.integration.rei.VeganOptionClientPlugin;

import java.util.List;

public class UsageDescriptionDisplay extends DescriptionDisplay
{
	public UsageDescriptionDisplay(ItemStack topic, List<ItemStack> related, List<EntryIngredient> referenced, List<FormattedCharSequence> text, boolean isFirstPage)
	{
		super(topic, related, referenced, text, isFirstPage);
	}

	@Override
	public List<EntryIngredient> getInputEntries()
	{
		return List.of(EntryIngredients.of(getTopic()));
	}

	@Override
	public List<EntryIngredient> getOutputEntries()
	{
		return getRelated().stream().map(EntryIngredients::of).toList();
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier()
	{
		return VeganOptionClientPlugin.Categories.WIKI_USAGE;
	}
}
