package squeek.veganoption.integration.jei.description;

import net.minecraft.item.ItemStack;
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;

import java.util.ArrayList;
import java.util.List;

public class CraftingDescMaker extends DescriptionMaker
{
	public static List<CraftingDescWrapper> getRecipes()
	{
		CraftingDescMaker maker = new CraftingDescMaker();
		List<CraftingDescWrapper> recipes = new ArrayList<CraftingDescWrapper>();

		for (ItemStack itemStack : DescriptionRegistry.itemStacksWithCraftingDescriptions)
		{
			List<CraftingDescWrapper> pages = maker.create(CraftingDescWrapper.class, itemStack);
			recipes.addAll(pages);
		}

		return recipes;
	}

	@Override
	public List<ItemStack> getRelated(ItemStack itemStack)
	{
		return RelationshipRegistry.getParents(itemStack);
	}

	@Override
	public String getText(ItemStack itemStack)
	{
		return getCraftingOfItemStack(itemStack);
	}

	@Override
	public String getRelatedText(ItemStack itemStack)
	{
		return getUsageOfItemStack(itemStack);
	}
}
