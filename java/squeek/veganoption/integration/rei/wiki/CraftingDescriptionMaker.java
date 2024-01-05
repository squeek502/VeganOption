package squeek.veganoption.integration.rei.wiki;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.world.item.ItemStack;
import squeek.veganoption.content.registry.RelationshipRegistry;

import java.util.List;

public class CraftingDescriptionMaker extends DescriptionMaker
{
	@Override
	public List<ItemStack> getRelatedItems(ItemStack topic)
	{
		return RelationshipRegistry.getParents(topic.getItem()).stream().map(ItemStack::new).toList();
	}

	@Override
	public String getText(ItemStack topic)
	{
		return getCraftingOfItem(topic);
	}

	@Override
	public String getRelatedText(ItemStack topic)
	{
		return getUsageOfItem(topic);
	}

	@Override
	public DescriptionDisplay newDisplay(ItemStack topic, List<ItemStack> related, List<EntryIngredient> referenced, String fullEntryText, int startingLineIndex, int endingLineIndex, boolean isFirstPage)
	{
		return new CraftingDescriptionDisplay(topic, related, referenced, fullEntryText, startingLineIndex, endingLineIndex, isFirstPage);
	}
}
