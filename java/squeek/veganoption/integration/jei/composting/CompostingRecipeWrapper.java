package squeek.veganoption.integration.jei.composting;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import squeek.veganoption.content.registry.CompostRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompostingRecipeWrapper extends BlankRecipeWrapper
{
	public final ItemStack output;
	public final int numGreens;
	public final int numBrowns;

	public CompostingRecipeWrapper(ItemStack output, int numGreens, int numBrowns)
	{
		this.output = output;
		this.numBrowns = numBrowns;
		this.numGreens = numGreens;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients)
	{
		List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();

		for (int i=0; i<numGreens; i++)
		{
			List<ItemStack> listToAdd = CompostRegistry.greens;
			if (i > 0)
			{
				// cycle the list so that the item cycles don't show the same item at the same time
				listToAdd = new ArrayList<ItemStack>(listToAdd);
				ItemStack first = listToAdd.remove(0);
				listToAdd.add(first);
			}
			inputs.add(listToAdd);
		}

		for (int i=0; i<numBrowns; i++)
			inputs.add(CompostRegistry.browns);

		ingredients.setInputLists(ItemStack.class, inputs);

		ingredients.setOutput(ItemStack.class, output);
	}
}
