package squeek.veganoption.integration.rei.wiki;

import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class DescriptionDisplay implements Display
{
	private final ItemStack topic;
	private final List<ItemStack> related;
	private final List<EntryIngredient> referenced;
	private final List<FormattedCharSequence> text;
	private final boolean isFirstPage;

	public DescriptionDisplay(ItemStack topic, List<ItemStack> related, List<EntryIngredient> referenced, List<FormattedCharSequence> text, boolean isFirstPage)
	{
		this.topic = topic;
		this.related = related;
		this.referenced = referenced;
		this.text = text;
		this.isFirstPage = isFirstPage;
	}

	public ItemStack getTopic()
	{
		return topic;
	}

	public List<ItemStack> getRelated()
	{
		return related;
	}

	public List<EntryIngredient> getReferenced()
	{
		return referenced;
	}

	public List<FormattedCharSequence> getText()
	{
		return text;
	}

	public boolean isFirstPage()
	{
		return isFirstPage;
	}
}
