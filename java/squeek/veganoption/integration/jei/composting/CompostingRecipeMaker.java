package squeek.veganoption.integration.jei.composting;

import net.minecraft.item.ItemStack;
import squeek.veganoption.content.modules.Composting;

import java.util.ArrayList;
import java.util.List;

public class CompostingRecipeMaker
{
	public static List<CompostingRecipeWrapper> getRecipes()
	{
		List<CompostingRecipeWrapper> recipes = new ArrayList<CompostingRecipeWrapper>();

		recipes.add(new CompostingRecipeWrapper(new ItemStack(Composting.compost), 2, 1));
		recipes.add(new CompostingRecipeWrapper(new ItemStack(Composting.rottenPlants), 1, 0));

		return recipes;
	}
}
