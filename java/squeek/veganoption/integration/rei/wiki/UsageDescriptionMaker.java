package squeek.veganoption.integration.rei.wiki;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import squeek.veganoption.content.registry.RelationshipRegistry;

import java.util.List;

public class UsageDescriptionMaker extends DescriptionMaker
{
	@Override
	public List<ItemStack> getRelated(ItemStack topic)
	{
		return RelationshipRegistry.getChildren(topic.getItem()).stream().map(ItemStack::new).toList();
	}

	@Override
	public String getText(ItemStack topic)
	{
		return getUsageOfItem(topic);
	}

	@Override
	public String getRelatedText(ItemStack topic)
	{
		return getCraftingOfItem(topic);
	}

	@Override
	public DescriptionDisplay newDisplay(ItemStack topic, List<ItemStack> related, List<EntryIngredient> referenced, List<FormattedCharSequence> text, boolean isFirstPage)
	{
		return new UsageDescriptionDisplay(topic, related, referenced, text, isFirstPage);
	}
}
