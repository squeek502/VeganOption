package squeek.veganoption.integration.jei.description;

import net.minecraft.item.ItemStack;
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;

import java.util.ArrayList;
import java.util.List;

public class UsageDescMaker extends DescriptionMaker
{
	public static List<UsageDescWrapper> getRecipes()
	{
		UsageDescMaker maker = new UsageDescMaker();
		List<UsageDescWrapper> recipes = new ArrayList<UsageDescWrapper>();

		for (ItemStack itemStack : DescriptionRegistry.itemStacksWithUsageDescriptions)
		{
			List<UsageDescWrapper> pages = maker.create(UsageDescWrapper.class, itemStack);
			recipes.addAll(pages);
		}

		return recipes;
	}

	@Override
	public List<ItemStack> getRelated(ItemStack itemStack)
	{
		return RelationshipRegistry.getChildren(itemStack);
	}

	@Override
	public String getText(ItemStack itemStack)
	{
		return getUsageOfItemStack(itemStack);
	}

	@Override
	public String getRelatedText(ItemStack itemStack)
	{
		return getCraftingOfItemStack(itemStack);
	}
}
