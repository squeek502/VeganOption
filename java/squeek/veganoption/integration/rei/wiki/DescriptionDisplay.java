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
	private final String fullEntryText;
	private final boolean isFirstPage;
	private final int startingLineIndex;
	private final int endingLineIndex;

	public DescriptionDisplay(ItemStack topic, List<ItemStack> related, List<EntryIngredient> referenced, String fullEntryText, int startingLineIndex, int endingLineIndex, boolean isFirstPage)
	{
		this.topic = topic;
		this.related = related;
		this.referenced = referenced;
		this.fullEntryText = fullEntryText;
		this.isFirstPage = isFirstPage;
		this.startingLineIndex = startingLineIndex;
		this.endingLineIndex = endingLineIndex;
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

	public String getFullEntryText()
	{
		return fullEntryText;
	}

	public boolean isFirstPage()
	{
		return isFirstPage;
	}

	public List<FormattedCharSequence> getThisPageText(String entryText)
	{
		return DescriptionMaker.splitText(entryText, DescriptionMaker.FONT, DescriptionMaker.WIDTH - DescriptionMaker.PADDING * 2).subList(startingLineIndex, endingLineIndex);
	}
}
